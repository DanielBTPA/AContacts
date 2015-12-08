package br.alphap.acontacts.io.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import br.alphap.acontacts.util.PersonalContact;

/**
 * Created by danielbt on 01/12/15.
 */
public class ADatabaseManager {

    private ADatabaseOpenHelper getDB;

    public ADatabaseManager(Context context) {
        getDB = new ADatabaseOpenHelper(context);
    }

    public void insert(PersonalContact newContact) {
        SQLiteDatabase db = getDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[1], newContact.getName());
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[2], newContact.getPhone());
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[3], encodeBitmap(newContact.getImageData()));
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[4], newContact.getContactType());

        db.insert(ADatabaseOpenHelper.TABLE_CONTACTS, null, values);
        db.close();
    }

    public void replace(int pos, PersonalContact newContact) {
        List<PersonalContact> list = getData();
        SQLiteDatabase db = getDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[0], list.get(pos).getContactId());
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[1], newContact.getName());
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[2], newContact.getPhone());
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[3], encodeBitmap(newContact.getImageData()));
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[4], newContact.getContactType());
        db.replace(ADatabaseOpenHelper.TABLE_CONTACTS, null, values);
        db.close();
    }

    public void delete(int pos) {
        List<PersonalContact> list = getData();
        SQLiteDatabase db = getDB.getWritableDatabase();
        db.delete(ADatabaseOpenHelper.TABLE_CONTACTS, "_id = ?", new String[]{"" + list.get(pos).getContactId()});
        db.close();
    }

    public PersonalContact get(int pos) {
        List<PersonalContact> list = getData();
        return list.get(pos);
    }

    public Cursor queryDatabase() {
        SQLiteDatabase db = getDB.getReadableDatabase();
        Cursor cursor = db.query(ADatabaseOpenHelper.TABLE_CONTACTS, ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS, null,
                null, null, null, null);
        return cursor;
    }

    public List<PersonalContact> getData() {
        List<PersonalContact> list = new ArrayList<>();
        Cursor cursor = queryDatabase();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                PersonalContact contact = new PersonalContact();
                contact.setContactId(cursor.getInt(0));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setImageData(decodeBitmap(cursor.getBlob(3)));
                contact.setContactType(cursor.getInt(4));

                list.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public int size() {
        return queryDatabase().getCount();
    }

    public boolean isEmpty() {
        return queryDatabase().getCount() <= 0;
    }

    public SQLiteOpenHelper getSqLiteOpenHelper() {
        return getDB;
    }

    private byte[] encodeBitmap(Bitmap b) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (b != null) {
            b.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        }

        return (byteArrayOutputStream.size() == 0) ? null : byteArrayOutputStream.toByteArray();
    }

    private Bitmap decodeBitmap(byte[] data) {
        Bitmap newImage = null;

        if (data != null) {
            newImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        return newImage;
    }

}
