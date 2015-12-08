package br.alphap.acontacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteOpenHelper;
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

import br.alphap.acontacts.io.database.ADatabaseManager;
import br.alphap.acontacts.io.database.ADatabaseOpenHelper;
import br.alphap.acontacts.manager.ManagerContactActivity;
import br.alphap.acontacts.util.PersonalContact;
import br.alphap.acontacts.util.PersonalContactAdapter;
import br.alphap.acontacts.util.RecyclerViewScrollDetector;

public class MainActivity extends AppCompatActivity implements PersonalContactAdapter.OnItemClickListenerProvider {

    private RecyclerView recyclerView;
    private PersonalContactAdapter adapter;
    private FloatingActionButton fab;

    private ADatabaseManager databaseManager;

    private ADatabaseOpenHelper openHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_main);

        openHelper = new ADatabaseOpenHelper(this);

        databaseManager = new ADatabaseManager(openHelper);

        recyclerView = (RecyclerView) findViewById(R.id.idRvListMain);
        recyclerView.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT || databaseManager.isEmpty()) {
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

    @Override
    protected void onStart() {
        super.onStart();

        adapter = new PersonalContactAdapter(this, databaseManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListenerProvider(this);

        adapter.setOnCardMenuItemClickListener(new PersonalContactAdapter.OnCardMenuItemListener() {
            @Override
            public boolean onItemSelected(MenuItem item, View view, final int position) {
                Intent intent = new Intent();
                String number = databaseManager.get(position).getPhone();

                if (item.getItemId() == R.id.idActionCardCall) {
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + number));
                } else if (item.getItemId() == R.id.idActionCardMessage) {
                    intent.setAction(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("sms:" + number));
                } else if (item.getItemId() == R.id.idActionCardEdit) {
                    intent.setClass(getBaseContext(), ManagerContactActivity.class);
                    intent.putExtra("contactData", (Parcelable) databaseManager.get(position));
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

                            if (databaseManager.isEmpty()) {
                                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            }

                            Snackbar sb = Snackbar.make(findViewById(R.id.idClFab),
                                    getMessageFormated(getResources().getString(R.string.abc_info_contact_deleted_unformated), position)
                                    , Snackbar.LENGTH_SHORT);

                            databaseManager.delete(position);
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
    }

    private String getMessageFormated(String modelMsg, int position) {
        PersonalContact contact = databaseManager.get(position);
        String nome = contact.getName();

        if (nome == null || nome.equals("")) {
            nome = contact.getPhone();
        }

        modelMsg = modelMsg.replace("N", "'" + nome + "'");

        return modelMsg;
    }

    @Override
    protected void onStop() {
        super.onStop();
        openHelper.close();
    }

    // Cria o menu na ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Retorna os dados obtidos a partir da ManagerContactActivity.class
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ManagerContactActivity.MANAGER_CONTACT_ADD_REQUEST) {
                PersonalContact contact = (PersonalContact) data.getParcelableExtra("contactData");
                databaseManager.insert(contact);

                Snackbar sb = Snackbar.make(findViewById(R.id.idClFab), getResources().getString(R.string.abc_info_contact_saved)
                        , Snackbar.LENGTH_LONG);
                sb.setActionTextColor(getResources().getColor(R.color.colorAccent));
                sb.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = databaseManager.size() - 1;
                        databaseManager.delete(pos);
                        adapter.notifyItemRemoved(pos);
                    }
                });
                sb.show();
            } else if (requestCode == ManagerContactActivity.MANAGER_CONTACT_EDIT_REQUEST) {
                final int position = data.getIntExtra("personalPosition", 0);
                final PersonalContact contact = (PersonalContact) data.getParcelableExtra("contactData");
                final PersonalContact oldContact = databaseManager.get(position);

                databaseManager.replace(position, contact);

                Snackbar sb = Snackbar.make(findViewById(R.id.idClFab), getResources().getString(R.string.abc_info_contact_edited)
                        , Snackbar.LENGTH_LONG);

                if (!contact.equals(oldContact)) {
                    sb.setActionTextColor(getResources().getColor(R.color.colorAccent));
                    sb.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            databaseManager.replace(position, oldContact);
                            adapter.notifyItemChanged(position);
                        }
                    });
                }
                sb.show();
            }
        }
    }

    // Click do FloatingActionButton
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
