package com.example.main_dialendar;

import static com.example.main_dialendar.DBHelper.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler {

    private final String TAG = "DBHandler";

    SQLiteOpenHelper mHelper = null;
    SQLiteDatabase mDB = null;

    public DBHandler(Context context, String name) {
        mHelper = new DBHelper(context, name, null, 1);
    }

    public static DBHandler open(Context context, String name) {
        return new DBHandler(context, name);
    }

    public Cursor select()
    {
        mDB = mHelper.getReadableDatabase();
        Cursor c = mDB.query(TB_NAME, null, null, null, null, null, null);
        return c;
    }

    public void insert(String name, byte[] image) {

        Log.d(TAG, "insert");

        mDB = mHelper.getWritableDatabase();

        ContentValues cv = new  ContentValues();
        cv.put(KEY_NAME,    name);
        cv.put(KEY_IMAGE,   image);

        mDB.insert(TB_NAME, null, cv);

    }

    public void delete(String name)
    {
        Log.d(TAG, "delete");
        mDB = mHelper.getWritableDatabase();
        mDB.delete(TB_NAME, "name=?", new String[]{name});
    }

    public void close() {
        mHelper.close();
    }
}
