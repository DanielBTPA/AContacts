package br.alphap.acontacts.manager;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import br.alphap.acontacts.R;
import br.alphap.acontacts.io.database.ADatabaseManager;
import br.alphap.acontacts.io.database.ADatabaseOpenHelper;
import br.alphap.acontacts.util.PersonalContact;
import br.alphap.acontacts.util.components.SearchableAdapter;

import static br.alphap.acontacts.util.components.PersonalContactAdapter.OnCardMenuItemListener;

/**
 * Created by danielbt on 09/12/15.
 */
public class SearchableActivity extends AppCompatActivity {


    private SearchableAdapter adapter;
    private RecyclerView recyclerView;
    private ADatabaseManager databaseManager;
    private ADatabaseOpenHelper openHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.idRvListSearchable);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        openHelper = new ADatabaseOpenHelper(this);

        databaseManager = new ADatabaseManager(openHelper, true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String query = getIntent().getStringExtra(SearchManager.QUERY);
        adapter = new SearchableAdapter(this, databaseManager, query);
        setTitle(getFormatedName(getResources().getString(R.string.abc_info_title_searchable_activity), query));

        adapter.setOnCardMenuItemClickListener(new OnCardMenuItemListener() {
            @Override
            public boolean onItemSelected(MenuItem item, View view, final int position) {
                Intent intent = new Intent();
                final List<PersonalContact> list = adapter.getResult();
  getSupportActionBar();              final PersonalContact contact = list.get(position);

                if (item.getItemId() == R.id.idActionCardCall) {
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contact.getPhone()));
                } else if (item.getItemId() == R.id.idActionCardMessage) {
                    intent.setAction(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("sms:" + contact.getPhone()));
                } else if (item.getItemId() == R.id.idActionCardEdit) {
                    intent.setClass(getBaseContext(), ManagerContactActivity.class);
                    intent.putExtra("contactData", contact);
                    intent.putExtra("contactManagerType", ManagerContactActivity.MANAGER_CONTACT_EDIT_REQUEST);
                    intent.putExtra("personalPosition", position);
                } else if (item.getItemId() == R.id.idActionCardDelete) {
                    AlertDialog.Builder alertEx = new AlertDialog.Builder(SearchableActivity.this);
                    alertEx.setMessage(getMessageFormated(getResources().getString(R.string.abc_info_contact_delete_unformated), position));
                    alertEx.setNegativeButton(getResources().getString(R.string.cancel), null);
                    alertEx.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Snackbar sb = Snackbar.make(findViewById(android.R.id.content),
                                    getMessageFormated(getResources().getString(R.string.abc_info_contact_deleted_unformated), position)
                                    , Snackbar.LENGTH_SHORT);

                            adapter.removeItemOnList(position, true);

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

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.searchable_menu, menu);

        SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);

        MenuItem item = menu.findItem(R.id.idMainActionSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.abc_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setTitle(getFormatedName(getResources().getString(R.string.abc_info_title_searchable_activity), query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        adapter.searchOnList(intent.getStringExtra(SearchManager.QUERY));
    }

    @Override
    protected void onStop() {
        super.onStop();
        openHelper.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ManagerContactActivity.MANAGER_CONTACT_EDIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                final int position = data.getIntExtra("personalPosition", 0);
                final PersonalContact contact = data.getParcelableExtra("contactData");
                final PersonalContact oldContact = databaseManager.get(position);

                adapter.replaceItemOnList(position, contact, true);

                Snackbar sb = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.abc_info_contact_edited)
                        , Snackbar.LENGTH_LONG);

                if (!contact.equals(oldContact)) {
                    sb.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    sb.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.replaceItemOnList(position, oldContact, true);
                        }
                    });
                }
                sb.show();
            }
        }
    }

    private String getMessageFormated(String modelMsg, int position) {
        PersonalContact contact = adapter.getResult().get(position);
        String nome = contact.getName();

        if (nome == null || nome.equals("")) {
            nome = contact.getPhone();
        }

        modelMsg = modelMsg.replace("N", "'" + nome + "'");

        return modelMsg;
    }

    private String getFormatedName(String modelMsg, String query) {

        return modelMsg.replace("N", query);
    }
}
