package com.example.main_dialendar;

import static com.example.main_dialendar.DBHelper.*;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.w3c.dom.Text;

public class DBHandler {

    private final String TAG = "DBHandler";

    SQLiteOpenHelper myHelper = null;
    SQLiteDatabase myDB = null;

    public DBHandler(Context context) {
        myHelper = new DBHelper(context);
    }

    public static DBHandler open(Context context) {
        return new DBHandler(context);
    }

    // CRUD = ISUD
    public void insert(String diary, byte[] image) {
        Log.d(TAG, "insert");

        // Gets the data repository in write mode
        myDB = myHelper.getWritableDatabase();

        // Greate a new map of values, where column names are the keys
        ContentValues cv = new  ContentValues();
        cv.put(COLUMN_DIARY,    diary);
        cv.put(COLUMN_IMAGE,   image);

        // Insert the new row, returning the primary key value of the new row
        myDB.insert(TABLE_NAME, null, cv);

    }

    public Cursor select()
    {
        myDB = myHelper.getReadableDatabase();
        Cursor c = myDB.query(TABLE_NAME,
                null,   // the array of columns. pass null to get all
                null,
                null,
                null,   // don't group the rows
                null,   // don't filter by row groups
                null    // sort order
        );
        return c;
    }

    public void update(String diary){
        Log.d(TAG, "update diary");

        myDB = myHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DIARY, diary);

        int count = myDB.update(
                TABLE_NAME,
                values,
                null,
                null);
    }

    // overriding update image
    public void update(byte[] image){
        Log.d(TAG, "update image");

        myDB = myHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE, image);

        int count = myDB.update(
                TABLE_NAME,
                values,
                null,
                null);
    }

    public void delete(String name) {
        Log.d(TAG, "delete");

        myDB = myHelper.getWritableDatabase();
        myDB.delete(TABLE_NAME, "name=?", new String[]{name});
    }

    public void close() {
        myHelper.close();
    }
}
