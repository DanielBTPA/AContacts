package br.alphap.acontacts;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

        databaseManager = new ADatabaseManager(openHelper, true);

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

        databaseManager.queryData();

        adapter = new PersonalContactAdapter(this, databaseManager);

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
                    intent.putExtra("contactData", databaseManager.get(position));
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

                            adapter.removeItemOnList(position);
                            sb.show();
                        }
                    });
                    alertEx.show();
                }

                try {
                    startActivityForResult(intent, ManagerContactActivity.MANAGER_CONTACT_EDIT_REQUEST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.setAdapter(adapter);
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

        SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);

        final MenuItem item = menu.findItem(R.id.idMainActionSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.abc_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                item.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    // Retorna os dados obtidos a partir da ManagerContactActivity.class
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ManagerContactActivity.MANAGER_CONTACT_ADD_REQUEST) {
                PersonalContact contact = data.getParcelableExtra("contactData");
                adapter.addItemOnList(contact);

                Snackbar sb = Snackbar.make(findViewById(R.id.idClFab), getResources().getString(R.string.abc_info_contact_saved)
                        , Snackbar.LENGTH_LONG);
                sb.setActionTextColor(getResources().getColor(R.color.colorAccent));
                sb.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = databaseManager.size() - 1;
                        adapter.removeItemOnList(pos);
                    }
                });
                sb.show();
            } else if (requestCode == ManagerContactActivity.MANAGER_CONTACT_EDIT_REQUEST) {
                final int position = data.getIntExtra("personalPosition", 0);
                final PersonalContact contact = data.getParcelableExtra("contactData");
                final PersonalContact oldContact = databaseManager.get(position);

                adapter.replaceItemOnList(position, contact);

                Snackbar sb = Snackbar.make(findViewById(R.id.idClFab), getResources().getString(R.string.abc_info_contact_edited)
                        , Snackbar.LENGTH_LONG);

                if (!contact.equals(oldContact)) {
                    sb.setActionTextColor(getResources().getColor(R.color.colorAccent));
                    sb.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.replaceItemOnList(position, oldContact);
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
