package com.example.main_dialendar;

import static com.example.main_dialendar.DBHelper.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import javax.annotation.Nullable;

public class DBHandler {

    private final String TAG = "DBHandler";
    private Context context;

    DBHelper myHelper = null;
    SQLiteDatabase myDB = null;

    public DBHandler(Context context) {
        myHelper = new DBHelper(context);
        myDB = myHelper.getWritableDatabase();
        myHelper.onCreate(myDB);
    }

    public static DBHandler open(Context context) {
        return new DBHandler(context);
    }

    /* CRUD = ISUD (SQL) */

    /**
     * insert
     * @param date
     * @param diary
     * @param imageView
     */
    public void insert(String date, String diary, byte[] imageView) {
        Log.d(TAG, "insert");

        // 쓰기모드로 열기
        myDB = myHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        // (컬럼이름, 데이터)
        ContentValues cv = new ContentValues();
        cv.put(_DATE, date);
        cv.put(COLUMN_DIARY, diary);
        cv.put(COLUMN_IMAGE, imageView);

        // Insert the new row, returning the primary key value of the new row
        Long newPrimaryKey = myDB.insert(TABLE_NAME, null, cv);

    }

    /**
     * select
     * @param date
     * @return
     */
    public Cursor select(String date) {
        Log.d(TAG, "select");

        myDB = myHelper.getReadableDatabase();
//        Cursor c = myDB.query(TABLE_NAME,
//                null,   // the array of columns. pass null to get all
//                date,
//                null,
//                null,   // don't group the rows
//                null,   // don't filter by row groups
//                null    // sort order
//        );
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + _DATE + " = ?;";
        Cursor c = myDB.rawQuery(sql, new String[]{date});
        return c;
    }

    /**
     * update
     * @param date
     * @param diary
     */
    public void updateTxt(String date, Editable diary) {
        Log.d(TAG, "update diary");

        myDB = myHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DIARY, String.valueOf(diary));

        int updatetedRows = myDB.update(
                TABLE_NAME,
                cv,
                _DATE + "=?", // "date=?"
                new String[]{date});
    }

    /**
     * update Image
     * @param date
     * @param image
     */
    public void updateImg(String date, byte[] image) {
        Log.d(TAG, "update image");

        myDB = myHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IMAGE, image);

        int updatetedRows = myDB.update(
                TABLE_NAME,
                cv,
                _DATE + "=?", // "date=?"
                new String[] {date});
    }

    /**
     * delete
     * @param date
     */
    public void delete(String date) {
        Log.d(TAG, "delete");

        myDB = myHelper.getWritableDatabase();
        int deletedRows = myDB.delete(TABLE_NAME,
                "name=?",
                new String[]{date});
    }

    /**
     * DB close
     */
    public void close() {
        myHelper.close();
        myDB.close();
    }

    /* 이미지뷰 위젯에서 이미지 바이트코드 추출 */
    private byte[] getImage(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageInByte = baos.toByteArray();
        return imageInByte;
    }
}
