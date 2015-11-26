package br.alphap.acontacts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

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

    private boolean actionGridEnable = false;

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
            menuItemId = savedInstanceState.getInt("menuItemId");
            actionGridEnable = savedInstanceState.getBoolean("actionGridEnable", false);
        } else {
            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            menuItemId = sharedPreferences.getInt("menuItemId", R.id.idActionGridOff);
            actionGridEnable = sharedPreferences.getBoolean("actionGridEnable", false);

        }

        setContentView(R.layout.activity_list_main);
        recyclerView = (RecyclerView) findViewById(R.id.idRvListMain);
        recyclerView.setHasFixedSize(true);

        if (list.isEmpty()) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recyclerView.setLayoutManager(menuItemId == R.id.idActionGridOff ? new LinearLayoutManager(this) : new GridLayoutManager(this, 2));
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

    private String getTextEt(int position) {
        String nome = list.getContact(position).getName();

        if (nome == null || nome.equals("")) {
            nome = list.getContact(position).getPhone();
        }

        return nome;
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

                if (item.getItemId() == R.id.idActionCardCall) {
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + list.getContact(position).getPhone()));
                } else if (item.getItemId() == R.id.idActionCardEdit) {
                    intent.setClass(getBaseContext(), ManagerContactActivity.class);
                    intent.putExtra("contactData", (Parcelable) list.getContact(position));
                    intent.putExtra("contactManagerType", ManagerContactActivity.MANAGER_CONTACT_EDIT_REQUEST);
                    intent.putExtra("personalPosition", position);
                } else if (item.getItemId() == R.id.idActionCardDelete) {
                    fab.show();

                    AlertDialog.Builder alertEx = new AlertDialog.Builder(MainActivity.this);
                    final String name = getTextEt(position);

                    alertEx.setMessage("Deseja remover '" + name + "' dos contatos?");
                    alertEx.setNegativeButton("Cancelar", null);
                    alertEx.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            list.removeContact(position);
                            adapter.notifyItemRemoved(position);

                            if (list.isEmpty()) {
                                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                actionGridEnable = false;
                                invalidateOptionsMenu();
                            }

                            View viewContent = findViewById(R.id.idClFab);

                            Snackbar sb = Snackbar.make(viewContent, "Contato '" + name + "' excluido.", Snackbar.LENGTH_SHORT);
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

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("actionGridEnable", actionGridEnable)
                .putInt("menuItemId", menuItemId).commit();

        Data.writeData(PATH_DEFAULT_CONTACTS, list);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("savedList", list);
        outState.putInt("menuItemId", menuItemId);
        outState.putBoolean("actionGridEnable", actionGridEnable);
    }

    // Menu Principal
    private Menu menu;
    private int menuItemId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItemId = setItemGridMode(actionGridEnable, menu, menuItemId).get("stateId");
        return super.onPrepareOptionsMenu(menu);
    }

    public void onClickActionModifyGrid(MenuItem item) {
        menuItemId = setItemGridMode(actionGridEnable, menu, item.getItemId()).get("stateId");

        if (menuItemId == R.id.idActionGridOn) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

    }

    private HashMap<String, Integer> setItemGridMode(boolean enable, Menu item, int stateItemId) {
        HashMap<String, Integer> stateObject = new HashMap<>();
        stateObject.put("stateId", stateItemId);

        if (enable) {
            switch (stateItemId) {
                case R.id.idActionGridOn:
                    item.getItem(0).setVisible(false);
                    item.getItem(1).setVisible(true);
                    stateObject.put("inverseStateId", R.id.idActionGridOff);
                    break;

                case R.id.idActionGridOff:
                    item.getItem(0).setVisible(true);
                    item.getItem(1).setVisible(false);
                    stateObject.put("inverseStateId", R.id.idActionGridOn);
                    break;
            }
        } else {
            item.getItem(0).setVisible(false);
            item.getItem(1).setVisible(false);
        }

        return stateObject;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ManagerContactActivity.MANAGER_CONTACT_ADD_REQUEST) {
                PersonalContact contact = (PersonalContact) data.getParcelableExtra("contactData");
                list.putContact(contact);

                actionGridEnable = true;
                invalidateOptionsMenu();

            } else if (requestCode == ManagerContactActivity.MANAGER_CONTACT_EDIT_REQUEST) {
                PersonalContact contact = (PersonalContact) data.getParcelableExtra("contactData");
                list.replaceContact(data.getIntExtra("personalPosition", 0), contact);
            }
            Toast.makeText(this, getResources().getText(R.string.stManagerToastSavedContact), Toast.LENGTH_SHORT).show();
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
