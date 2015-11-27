package br.alphap.acontacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;

import br.alphap.acontacts.manager.ManagerContactActivity;
import br.alphap.acontacts.util.Data;
import br.alphap.acontacts.util.PersonalContact;
import br.alphap.acontacts.util.PersonalContactAdapter;
import br.alphap.acontacts.util.PersonalContactList;
import br.alphap.acontacts.util.RecyclerViewScrollDetector;

public class MainActivity extends AppCompatActivity implements PersonalContactAdapter.OnItemClickListenerProvider {

    private RecyclerView recyclerView;
    private PersonalContactList list;
    private PersonalContactAdapter adapter;
    private FloatingActionButton fab;

    public static final String PATH_DEFAULT_CONTACTS = "AContacts/contacts.pc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Data.isExistFile(PATH_DEFAULT_CONTACTS)) {
            list = new PersonalContactList();
            Data.createFilePath(PATH_DEFAULT_CONTACTS);
        } else {
            try {
                if (list == null) {
                    list = (PersonalContactList) Data.readData(PATH_DEFAULT_CONTACTS);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                list = new PersonalContactList();
                e.printStackTrace();
            }
        }

        if (savedInstanceState != null) {
            list = (PersonalContactList) savedInstanceState.getParcelable("savedList");
        }

        setContentView(R.layout.activity_list_main);
        recyclerView = (RecyclerView) findViewById(R.id.idRvListMain);
        recyclerView.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT || list.isEmpty()) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }

        fab = (FloatingActionButton) findViewById(R.id.idFbAddContact);

        recyclerView.addOnScrollListener(new RecyclerViewScrollDetector() {
            @Override
            public void onScrollUp() {
                fab.hide();
            }

            @Override
            public void onScrollDown() {
                fab.show();
            }

            @Override
            public void setScrollThreshold() {

            }

        });

    }

    private String getMessageFormated(String modelMsg, int position) {
        String nome = list.getContact(position).getName();

        if (nome == null || nome.equals("")) {
            nome = list.getContact(position).getPhone();
        }

        modelMsg = modelMsg.replace("N", "'" + nome + "'");

        return modelMsg;
    }

    @Override
    protected void onStart() {
        super.onStart();


        adapter = new PersonalContactAdapter(this, list);
        adapter.setOnItemClickListenerProvider(this);

        adapter.setOnCardMenuItemClickListener(new PersonalContactAdapter.OnCardMenuItemListener() {
            @Override
            public boolean onItemSelected(MenuItem item, View view, final int position) {
                Intent intent = new Intent();
                String number = list.getContact(position).getPhone();


                if (item.getItemId() == R.id.idActionCardCall) {
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + number));
                } else if (item.getItemId() == R.id.idActionCardMessage) {
                    intent.setAction(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("sms:" + number));
                } else if (item.getItemId() == R.id.idActionCardEdit) {
                    intent.setClass(getBaseContext(), ManagerContactActivity.class);
                    intent.putExtra("contactData", (Parcelable) list.getContact(position));
                    intent.putExtra("contactManagerType", ManagerContactActivity.MANAGER_CONTACT_EDIT_REQUEST);
                    intent.putExtra("personalPosition", position);
                } else if (item.getItemId() == R.id.idActionCardDelete) {
                    fab.show();

                    AlertDialog.Builder alertEx = new AlertDialog.Builder(MainActivity.this);

                    alertEx.setMessage(getMessageFormated(getResources().getString(R.string.abc_info_contact_delete_unformated), position));
                    alertEx.setNegativeButton(getResources().getString(R.string.cancel), null);
                    alertEx.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (list.isEmpty()) {
                                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            }

                            View viewContent = findViewById(R.id.idClFab);

                            Snackbar sb = Snackbar.make(viewContent,
                                    getMessageFormated(getResources().getString(R.string.abc_info_contact_deleted_unformated), position)
                                    , Snackbar.LENGTH_SHORT);

                            list.removeContact(position);
                            adapter.notifyItemRemoved(position);

                            sb.show();
                        }
                    });
                    alertEx.show();
                }

                try {
                    startActivityForResult(intent, ManagerContactActivity.MANAGER_CONTACT_EDIT_REQUEST);
                } catch (Exception e) {
                }
                return false;
            }
        });

        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStop() {
        super.onStop();

        Data.writeData(PATH_DEFAULT_CONTACTS, list);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("savedList", list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ManagerContactActivity.MANAGER_CONTACT_ADD_REQUEST) {
                PersonalContact contact = (PersonalContact) data.getParcelableExtra("contactData");
                list.putContact(contact);

            } else if (requestCode == ManagerContactActivity.MANAGER_CONTACT_EDIT_REQUEST) {
                PersonalContact contact = (PersonalContact) data.getParcelableExtra("contactData");
                list.replaceContact(data.getIntExtra("personalPosition", 0), contact);
            }
            View viewContent = findViewById(R.id.idClFab);

            Snackbar sb = Snackbar.make(viewContent, getResources().getString(R.string.abc_info_contact_saved)
                    , Snackbar.LENGTH_LONG);
            sb.setActionTextColor(getResources().getColor(R.color.colorAccent));
            sb.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = list.size() - 1;
                    list.removeContact(pos);
                    adapter.notifyItemRemoved(pos);
                }
            });
            sb.show();
        }
    }

    public void onClickAddContact(View view) {
        Intent i = new Intent(this, ManagerContactActivity.class);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.putExtra("contactManagerType", ManagerContactActivity.MANAGER_CONTACT_ADD_REQUEST);
        this.startActivityForResult(i, ManagerContactActivity.MANAGER_CONTACT_ADD_REQUEST);
    }

    @Override
    public void onClickItem(View v, int position) {

    }
}
