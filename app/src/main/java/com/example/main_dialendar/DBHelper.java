package com.example.main_dialendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    // 데이터 베이스 정보 상수
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dialendar.db";
    public static final String TABLE_NAME = "dialendar";
    public static final String _ID = "_date";
    public static final String COLUMN_DIARY = "diary"; //text
    public static final String COLUMN_IMAGE = "image"; //blob 비트맵 이미지 저장 타입

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE if not exists " + TABLE_NAME + "("
                + _ID + "integer primary key autoincrement,"
                + COLUMN_DIARY + " TEXT,"
                + COLUMN_IMAGE + " BLOB);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE if exists " + TABLE_NAME;

        db.execSQL(sql);
        onCreate(db);
    }

}