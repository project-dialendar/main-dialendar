package com.example.main_dialendar.view.activity;

import static com.example.main_dialendar.DBHelper.COLUMN_DIARY;
import static com.example.main_dialendar.DBHelper.COLUMN_IMAGE;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.BitmapDrawable;
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

import com.amitshekhar.DebugDB;
import com.bumptech.glide.Glide;
import com.example.main_dialendar.DBHandler;
//import com.bumptech.glide.Glide;
import com.example.main_dialendar.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryActivity extends AppCompatActivity {

    /* 이미지 불러올때 필요한 변수 */
    private static final int REQUEST_CODE = 0;

    /* 위젯 */
    private ImageView btn_diary_options, btn_diary_photo, btn_save_back;
    private TextView tv_diary_date;
    private EditText et_diary;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd. hh:mm");

    /* 데이터베이스 */
    private DBHandler dbHandler;
    private static boolean dataExist = Boolean.parseBoolean(null);

//    @Override
//    public void onBackPressed() { // 뒤로가기 버튼으로 액티비티 나가도 저장
//        super.onBackPressed();
//        // 데이터 저장
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        DebugDB.getAddressLog();

        // 데이터 베이스 연결
        dbHandler = DBHandler.open(DiaryActivity.this);
        // 데이터 열 생성
//        dbHandler.insert(tv_diary_date.getText().toString(), null, null);
        dbHandler.insert(tv_diary_date.getText().toString(), et_diary.getText().toString(), getImage(btn_diary_photo));

        // 위젯 정의
        btn_diary_options = findViewById(R.id.btn_diary_options);
        btn_diary_photo = findViewById(R.id.btn_diary_photo);
        btn_save_back = findViewById(R.id.btn_save_back);

        tv_diary_date = findViewById(R.id.tv_diary_date);
        et_diary = findViewById(R.id.et_diary);

        // 일기 날짜 세팅
        Intent diaryIntent = getIntent();
        boolean isToday = diaryIntent.getBooleanExtra("today", true);
        if (isToday)
            tv_diary_date.setText(getTime());
        else {
            String date = diaryIntent.getStringExtra("date");
            tv_diary_date.setText(date);
        }

        // 이미지 버튼 -> 갤러리에서 사진 불러오기
        btn_diary_photo.setOnClickListener(v -> pickFromGallery());

        // 달력 화면으로 나가면서 저장
        btn_save_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Cursor c = dbHandler.select((String) tv_diary_date.getText());
//                if (c == null) { // 기존 데이터 없음
//                    c.close();
//                    dbHandler.insert(tv_diary_date.getText().toString(), et_diary, btn_diary_photo);
//                } else { // c != null // 문제: 데이터가 없어도 커서가 null이 아님
//                    c.close();
//                    dbHandler.updateTxt(tv_diary_date.getText().toString(), et_diary.getText());
//                }

                dbHandler.updateTxt(tv_diary_date.getText().toString(), et_diary.getText());
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
                        switch (menuItem.getItemId()) {
                            case R.id.share_diary:
                                // 1. 한 장의 폴라로이드 같은 이미지로 공유

                            case R.id.delete_diary:
                                // 2. 일기 삭제
                                dbHandler.delete(tv_diary_date.toString());
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

//        Cursor c = dbHandler.select(tv_diary_date.getText().toString());
//        if (c != null) {
//            // 데이터베이스에서 날짜에 해당하는 데이터 읽어 화면에 보여주기
//            while (c.moveToNext()) {//여기보세요
//                et_diary.setText(c.getExtras().getCharSequence(COLUMN_DIARY));
//                int imageIndex = c.getColumnIndex(COLUMN_IMAGE);
//                if (imageIndex > 0) {
//                    Glide.with(getApplicationContext())
//                            .load(c.getBlob(imageIndex))
//                            .override(400, 400)
//                            .into(btn_diary_photo);
//                }
//            }
//        }
//        c.close();
    }

    /**
     * 이거 넣으면 앱 강종
     * 액티비티 열릴 때 마다 데이터베이스에 데이터 있는지 확인
     * 이미 데이터 있으면 읽어와서 위젯에 적용
     */
//    protected void onStart() {
//        super.onStart();
//        Cursor c = dbHandler.select(tv_diary_date.getText().toString());
//        if (c != null) {
//            // 데이터베이스에서 날짜에 해당하는 데이터 읽어 화면에 보여주기
//            while(c.moveToNext()){//여기보세요
//                et_diary.setText(c.getExtras().getCharSequence(COLUMN_DIARY));
//                int imageIndex = c.getColumnIndex(COLUMN_IMAGE);
//                if(imageIndex > 0){
//                    Glide.with(getApplicationContext())
//                            .load(c.getBlob(imageIndex))
//                            .override(400, 400)
//                            .into(btn_diary_photo);
//                }
//            }
//        }
//        c.close();
//    }
    @Override
    protected void onStop() {
        // 데이터베이스 해제
        super.onStop();
        dbHandler.close();
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
     * 갤러리에서 사진 가져오는 메소드
     */
    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    dbHandler.updateImg(tv_diary_date.getText().toString(), bytes);
                    in.close();

                    Glide.with(getApplicationContext()).load(img).override(400, 400).into(btn_diary_photo);

//                    btn_diary_photo.setImageBitmap(img);
                } catch (Exception e) {

                }
//                Glide.with(getApplicationContext()).load(data.getData()).override(400, 400).into(btn_diary_photo);

//                // 데이터베이스에 이미지만 업데이트
//                dbHandler.update(data.getData());

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
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
