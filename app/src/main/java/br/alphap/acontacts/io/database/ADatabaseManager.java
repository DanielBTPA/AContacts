package br.alphap.acontacts.io.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.alphap.acontacts.util.PersonalContact;
import br.alphap.acontacts.util.PersonalContactList;

/**
 * Created by danielbt on 01/12/15.
 */
public class ADatabaseManager {

    private ADatabaseOpenHelper getDB;


    public ADatabaseManager(Context context) {
        getDB = new ADatabaseOpenHelper(context);
    }

    public PersonalContactList insert(PersonalContact newContact) {
        SQLiteDatabase db = getDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[1], newContact.getName());
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[2], newContact.getPhone());
        // values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[3], newContact.getImageData());
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[4], newContact.getContactType());

        db.insert(ADatabaseOpenHelper.TABLE_CONTACTS, null, values);
        db.close();

        return getData();
    }

    public void replace(int pos, PersonalContact newContact, PersonalContactList listRecent) {
        PersonalContactList list = getData();
        SQLiteDatabase db = getDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[0], list.getContact(pos).getContactId());
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[1], newContact.getName());
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[2], newContact.getPhone());
        // values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[3], contact.getImageData());
        values.put(ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS[4], newContact.getContactType());
        db.replace(ADatabaseOpenHelper.TABLE_CONTACTS, null, values);
        listRecent.replaceContact(pos, newContact);
        db.close();
    }

    public void delete(int pos, PersonalContactList list) {
        SQLiteDatabase db = getDB.getWritableDatabase();
        db.delete(ADatabaseOpenHelper.TABLE_CONTACTS, "_id = ?", new String[] {""+list.getContact(pos).getContactId()});
        list.removeContact(pos);
        db.close();
    }

    public Cursor queryDatabase() {
        SQLiteDatabase db = getDB.getReadableDatabase();
        Cursor cursor = db.query(ADatabaseOpenHelper.TABLE_CONTACTS, ADatabaseOpenHelper.COLUMNS_TABLE_CONTACTS, null,
                null, null, null, null);
        return cursor;
    }

    public PersonalContactList getData() {
        PersonalContactList list = new PersonalContactList();
        Cursor cursor = queryDatabase();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                PersonalContact contact = new PersonalContact();
                contact.setContactId(cursor.getInt(0));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setImageData(cursor.getBlob(3));
                contact.setContactType(cursor.getInt(4));

                list.putContact(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

}
