package br.alphap.acontacts.manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
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

import java.util.ArrayList;
import java.util.List;

import br.alphap.acontacts.R;
import br.alphap.acontacts.util.PersonalContact;

public class ManagerContactActivity extends AppCompatActivity {

    public static final int MANAGER_CONTACT_ADD_REQUEST = 100;
    public static final int MANAGER_CONTACT_EDIT_REQUEST = 200;

    private static final int CHOOSE_OR_TAKE_PIC_REQUEST = 300;

    private static PersonalContact contact;

    private ImageView ivPersonalPic;
    private EditText edPersonalName, edPersonalPhone;
    private Spinner spContactType;

    private String[] phonetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            contact = (PersonalContact) savedInstanceState.getParcelable("savedContact");
        } else {
            contact = new PersonalContact();
        }

        setContentView(R.layout.activity_manager_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivPersonalPic = (ImageView) findViewById(R.id.idIvManagerPic);
        edPersonalName = (EditText) findViewById(R.id.idEtManagerName);

        edPersonalPhone = (EditText) findViewById(R.id.idEtManagerPhone);
        edPersonalPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        spContactType = (Spinner) findViewById(R.id.idSpManagerSelectedType);

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
            contact = (PersonalContact) getIntent().getParcelableExtra("contactData");
            edPersonalName.setText(contact.getName());
            edPersonalPhone.setText(contact.getPhone());
            spContactType.setSelection(contact.getContactType());
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
    protected void onResume() {
        super.onResume();

        if (contact.getImageData() != null) {
            final Bitmap bitmapOp = contact.getImageData();
            ivPersonalPic.post(new Runnable() {
                @Override
                public void run() {
                    Bitmap b = Bitmap.createScaledBitmap(bitmapOp, View.MeasureSpec.getSize(ivPersonalPic.getMeasuredWidth()),
                            View.MeasureSpec.getSize(ivPersonalPic.getMeasuredHeight()), true);
                    ivPersonalPic.setImageBitmap(b);
                }
            });

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("savedContact", contact);
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
        Intent intentClearImage = new Intent(this, RestoreToDefaultImage.class);
        intentClearImage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intentList.add(pickIntent);
        intentList.add(takePhotoIntent);

        if (contact.getImageData() != null) {
            intentList.add(intentClearImage);
        }

        chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 2),
                getResources().getString(R.string.abc_manager_textview_title_pic).replace(":", ""));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));

        return chooserIntent;
    }

    public void onClickChoosePicture(View view) {
        startActivityForResult(getPickImageIntent(), CHOOSE_OR_TAKE_PIC_REQUEST);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_OR_TAKE_PIC_REQUEST) {
            if (resultCode == RESULT_OK) {
               if (data != null) {
                   Bitmap getImageBitmap = null;

                   if (data.getExtras() != null) {
                       getImageBitmap = (Bitmap) data.getExtras().get("data");
                   } else {
                       if (data.getData() != null) {
                           Cursor cursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null, null);
                           cursor.moveToFirst();
                           String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                           getImageBitmap = BitmapFactory.decodeFile(path);
                       }

                       getImageBitmap = scaleDownBitmap(getImageBitmap, 120);
                   }
                   contact.setImageData(getImageBitmap);
               } else {
                   ivPersonalPic.setImageResource(R.drawable.personal_image);
                   contact.setImageData(null);
               }
            }
        }
    }

    public Bitmap scaleDownBitmap(Bitmap photo, int newHeight) {

        final float densityMultiplier = getResources().getDisplayMetrics().density;

        int h = (int) (newHeight * densityMultiplier);
        int w = (h * photo.getWidth() / photo.getHeight());

        photo = Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
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

        Bitmap bitmap = contact.getImageData();

        if (bitmap != null) {
            int size = (int) (70 * getResources().getDisplayMetrics().density);
            bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
            contact.setImageData(bitmap);
        }

        Intent intent = getIntent();
        intent.putExtra("contactData", contact);
        setResult(RESULT_OK, intent);
    }

    public static class RestoreToDefaultImage extends AppCompatActivity {

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
