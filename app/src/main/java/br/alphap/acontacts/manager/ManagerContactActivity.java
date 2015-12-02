package br.alphap.acontacts.manager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import br.alphap.acontacts.R;
import br.alphap.acontacts.util.PersonalContact;

public class ManagerContactActivity extends AppCompatActivity {

    public static final int MANAGER_CONTACT_ADD_REQUEST = 100;
    public static final int MANAGER_CONTACT_EDIT_REQUEST = 200;

    private ImageView ivPersonalPic;
    private EditText edPersonalName, edPersonalPhone;
    private Spinner spContactType;
    private PersonalContact contact;

    private String[] phonetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivPersonalPic = (ImageView) findViewById(R.id.idIvManagerPic);
        edPersonalName = (EditText) findViewById(R.id.idEtManagerName);

        edPersonalPhone = (EditText) findViewById(R.id.idEtManagerPhone);
        edPersonalPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        spContactType = (Spinner) findViewById(R.id.idSpManagerSelectedType);

        phonetype = getResources().getStringArray(R.array.spinnerTypes);

        Toast.makeText(this, phonetype[1], Toast.LENGTH_SHORT).show();

        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                phonetype);
        spContactType.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        int operation = getIntent().getIntExtra("contactManagerType", MANAGER_CONTACT_ADD_REQUEST);

        if (operation == MANAGER_CONTACT_ADD_REQUEST) {
            setTitle(getResources().getString(R.string.abc_manager_activity_title_add));
            if (contact == null) {
                contact = new PersonalContact();
            }
            spContactType.setSelection(contact.getContactType());
        } else if (operation == MANAGER_CONTACT_EDIT_REQUEST) {
            setTitle(getResources().getString(R.string.abc_cardview_info_action_edit));
            contact = (PersonalContact) getIntent().getParcelableExtra("contactData");
            edPersonalName.setText(contact.getName());
            edPersonalPhone.setText(contact.getPhone());
            spContactType.setSelection(contact.getContactType());
        }

        if (contact.getImageData() != null) {
            ivPersonalPic.setImageBitmap(null);
        }

        spContactType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                contact.setContactType(position);
                edPersonalPhone.setHint(phonetype[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("savedContact", contact);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        contact = (PersonalContact) savedInstanceState.getParcelable("savedContact");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manager_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.idManagerActionDone:
                if (!isEditTextEmpty(edPersonalPhone)) {
                    finalizateOperation();
                    finish();
                } else {
                    String msg = null;
                    switch (contact.getContactType()) {
                        case 0:
                            msg = getResources().getString(R.string.abc_info_without_number_1);
                            break;
                        case 1:
                            msg = getResources().getString(R.string.abc_info_without_number_2);
                    }
                    Toast.makeText(this,
                            msg, Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().equals("");
    }

    private void finalizateOperation() {
        contact.setName(edPersonalName.getText().toString());
        contact.setPhone(edPersonalPhone.getText().toString());

        Intent intent = getIntent();
        intent.putExtra("contactData", (Parcelable) contact);
        setResult(RESULT_OK, intent);
    }
}
