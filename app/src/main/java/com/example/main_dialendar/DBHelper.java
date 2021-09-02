package com.example.main_dialendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    // 데이터 베이스
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "dialendar.db";
    public static final String TB_NAME = "diary_table";
    // 비트맵 이미지 -> db 저장 변수 생성

    public DBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE if not exists mytable ("
                + "_date integer primary key autoincrement,"
                + "diary text);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE if exists mytable";

        db.execSQL(sql);
        onCreate(db);
    }

    // crud
}