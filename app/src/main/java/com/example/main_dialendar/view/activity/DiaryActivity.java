package com.example.main_dialendar.view.activity;

import static com.example.main_dialendar.DBHelper.DB_NAME;
import static com.example.main_dialendar.DBHelper.DB_VERSION;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main_dialendar.DBHelper;
import com.example.main_dialendar.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.InputStream;

public class DiaryActivity extends AppCompatActivity {

    // 이미지 불러올때 필요한 변수
    private static final int PICK_IMAGE = 1111;
    private static final int REQUEST_CODE = 0;

    // 위젯
    private ImageView btn_diary_options, btn_diary_photo, btn_save_back;
    private TextView tv_diary_date;
    private EditText et_diary;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd. hh:mm");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // 데이터 베이스
        DBHelper dbHelper;
        SQLiteDatabase db;
        dbHelper = new DBHelper(
                DiaryActivity.this,
                DB_NAME,
                null,
                DB_VERSION);
        db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);

        // 위젯 정의
        btn_diary_options = findViewById(R.id.btn_diary_options);
        btn_diary_photo = findViewById(R.id.btn_diary_photo);
        btn_save_back = findViewById(R.id.btn_save_back);

        tv_diary_date = findViewById(R.id.tv_diary_date);
        et_diary = findViewById(R.id.et_diary);

        Intent diaryIntent = getIntent();
        boolean isToday = diaryIntent.getBooleanExtra("today", true);
        if (isToday)
            tv_diary_date.setText(getTime());

        // 이미지 버튼 -> 갤러리에서 사진 불러오기
        btn_diary_photo.setOnClickListener(v -> pickFromGallery());

        // 달력 화면으로 나가면서 저장
        btn_save_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // db save
                finish();
            }
        });

        // 옵션 1.일기삭제 2. 공유
        btn_diary_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(DiaryActivity.this, btn_diary_options);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.share_diary:
                                // db read and make share image
                            case R.id.delete_diary:
                                // db delete
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

    }
  
    /***
    * 현재 시간 반환 메소드
    */
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        return mFormat.format(date);
    }

    /***
     * 사진 불러오기
     */
    private void pickFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    btn_diary_photo.setImageBitmap(img);
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }
}
