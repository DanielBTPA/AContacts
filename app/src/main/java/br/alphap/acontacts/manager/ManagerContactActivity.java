package br.alphap.acontacts.manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import br.alphap.acontacts.R;
import br.alphap.acontacts.util.PersonalContact;

public class ManagerContactActivity extends AppCompatActivity {

    public static final int MANAGER_CONTACT_ADD_REQUEST = 100;
    public static final int MANAGER_CONTACT_EDIT_REQUEST = 200;
    private static final int CHOOSE_OR_TAKE_PIC_REQUEST = 300;

    private PersonalContact contact;

    private ImageView ivPersonalPic;
    private EditText edPersonalName, edPersonalPhone;
    private TextInputLayout textInputLayoutPhone;
    private AppCompatSpinner spContactType;

    private String[] phonetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contact = getIntent().getParcelableExtra("contactData");

        setContentView(R.layout.activity_manager_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivPersonalPic = (ImageView) findViewById(R.id.idIvManagerPic);
        edPersonalName = (EditText) findViewById(R.id.idEtManagerName);

        edPersonalPhone = (EditText) findViewById(R.id.idEtManagerPhone);
        edPersonalPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        textInputLayoutPhone = (TextInputLayout) findViewById(R.id.textInputLayoutPhone);

        spContactType = (AppCompatSpinner) findViewById(R.id.idSpManagerSelectedType);

        phonetype = getResources().getStringArray(R.array.spinnerTypes);

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
            spContactType.setSelection(contact.getContactType());
        } else if (operation == MANAGER_CONTACT_EDIT_REQUEST) {
            setTitle(getResources().getString(R.string.abc_cardview_info_action_edit));
            edPersonalName.setText(contact.getName());
            edPersonalPhone.setText(contact.getPhone());
            spContactType.setSelection(contact.getContactType());
        }

        ivPersonalPic.setImageBitmap(contact.haveImage() ? contact.getImageAsBitmap() :
                BitmapFactory.decodeResource(getResources(), R.drawable.personal_image));

        spContactType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                contact.setContactType(position);
                textInputLayoutPhone.setHint(phonetype[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manager_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public Intent getPickImageIntent() {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intentClearImage = new Intent(this, ClearImageActivity.class);

        intentList.add(pickIntent);
        intentList.add(takePhotoIntent);

        if (contact.getImageData() != null) {
            intentList.add(intentClearImage);
        }

        chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 2),
                getResources().getString(R.string.abc_manager_textview_title_pic).replace(":", ""));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[intentList.size()]));

        return chooserIntent;
    }

    public void onClickChoosePicture(View view) {
        startActivityForResult(getPickImageIntent(), CHOOSE_OR_TAKE_PIC_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_OR_TAKE_PIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap newImage;
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    newImage = BitmapFactory.decodeStream(in);
                    newImage = Bitmap.createScaledBitmap(newImage, 240, 240, true);
                    contact.setImageAsBitmap(newImage);

                } catch (NullPointerException e) {
                    try {
                        newImage = (Bitmap) data.getExtras().get("data");
                        newImage = Bitmap.createScaledBitmap(newImage, 240, 240, true);
                        contact.setImageAsBitmap(newImage);
                    } catch (NullPointerException e1) {
                        contact.setImageData(null);
                    }
                } catch (FileNotFoundException e) {
                }
            }
        }
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
        contact.setImageData(contact.getImageData());

        Intent intent = new Intent();
        intent.putExtra("personalPosition", getIntent().getIntExtra("personalPosition", 0));
        intent.putExtra("contactData", contact);
        setResult(RESULT_OK, intent);
    }

    public static class ClearImageActivity extends AppCompatActivity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            this.setResult(RESULT_OK, null);
            Toast.makeText(this, getResources().getString(R.string.abc_info_photo_removed), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onResume() {
            super.onResume();
            finish();
        }
    }
}
