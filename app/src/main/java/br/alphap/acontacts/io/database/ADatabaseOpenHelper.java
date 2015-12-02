package br.alphap.acontacts.io.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 *
 * Created by danielbt on 30/11/15.
 */
public class ADatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String DB_CONTACTS_DEFAULT = "data.db";
    public static final String TABLE_CONTACTS = "tbContacts";
    public static final String[] COLUMNS_TABLE_CONTACTS = {"_id", "name", "phone", "picImage", "phoneType"};

    private static final int VERSION = 1;

    public ADatabaseOpenHelper(Context context) {
        super(context, DB_CONTACTS_DEFAULT, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableContacts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTableContacts(SQLiteDatabase db) {
        String query = "create table " + TABLE_CONTACTS + " (" +
                COLUMNS_TABLE_CONTACTS[0] + " integer primary key autoincrement," +
                COLUMNS_TABLE_CONTACTS[1] + " varchar(30)," +
                COLUMNS_TABLE_CONTACTS[2] + " varchar(15)," +
                COLUMNS_TABLE_CONTACTS[3] + " blob," +
                COLUMNS_TABLE_CONTACTS[4] + " integer);";
        db.execSQL(query);
    }

}
