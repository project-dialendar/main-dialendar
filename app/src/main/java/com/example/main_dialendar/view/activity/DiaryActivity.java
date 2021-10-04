package com.example.main_dialendar.view.activity;

import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.example.main_dialendar.R;
import com.example.main_dialendar.database.Diary;
import com.example.main_dialendar.database.DiaryDao;
import com.example.main_dialendar.database.DiaryDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryActivity extends AppCompatActivity {

    /* 위젯 */
    private ImageView btn_diary_options;
    private ImageView btn_diary_photo;
    private ImageView btn_save_back;
    private TextView tv_diary_date;
    private EditText et_diary;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd.");
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");

    Date d;

    /* 데이터베이스 */
    private DiaryDao mDiaryDao;
    private int imageNullCheck;

    /* 이미지 */
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        /* 데이터베이스 생성 */
        DiaryDatabase database = DiaryDatabase.getInstance(this);
        mDiaryDao = database.diaryDao();                  // 인터페이스 객체 할당

        // 위젯 정의
        btn_diary_options = findViewById(R.id.btn_diary_options);
        btn_diary_photo = findViewById(R.id.btn_diary_photo);
        btn_save_back = findViewById(R.id.btn_save_back);

        tv_diary_date = findViewById(R.id.tv_diary_date);
        et_diary = findViewById(R.id.et_diary);

        // 일기 날짜 세팅
        Intent diaryIntent = getIntent();
        d = new Date(System.currentTimeMillis());

        boolean isToday = diaryIntent.getBooleanExtra("today", true);
        if (isToday){
            tv_diary_date.setText(mFormat.format(d));
        }
        else {
            String date = diaryIntent.getStringExtra("date");
            try {
                d = mFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tv_diary_date.setText(date);
        }

        // 이미지 버튼 -> 갤러리에서 사진 불러오기
        btn_diary_photo.setOnClickListener(v -> pickFromGallery());

        // 해당 날짜에 레코드가 존재하는지 확인
        Diary diaryRecord = isExist();

        // 레코드 존재하면 화면에 보이게
        try {
            et_diary.setText(diaryRecord.getText());
            btn_diary_photo.setImageBitmap(getImageInBitmap(diaryRecord.getImage()));
            imageNullCheck = diaryRecord.getImageNullCheck();
        } catch (NullPointerException e) {

        }
//        if (diaryRecord != null) {
//            et_diary.setText(diaryRecord.getText());
//            btn_diary_photo.setImageBitmap(getImageInBitmap(diaryRecord.getImage()));
//        }

        // Main Activity로 나가면서 저장
        btn_save_back.setOnClickListener(v -> {
            if (diaryRecord == null) {
                insertRecord();
            } else { // 기존 레코드 존재
                updateRecord();
            }
            finish(); // 액티비티 종료
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
                                Diary deleteRecord = new Diary();
                                deleteRecord.setDate(dbFormat.format(d));

                                mDiaryDao.deleteDiary(deleteRecord);

                                finish(); // 액티비티 종료
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });
    }


    /**
     * 뒤로가기 버튼
     * 뒤로가기 버튼으로 diaryActivity 나가도 저장
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Diary diaryRecord = isExist();

        if (diaryRecord == null) {
            insertRecord();
        } else {
            updateRecord();
        }
        finish();
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
                    Diary diaryRecord = isExist();

                    /* 이미지뷰에 이미지 넣기 */
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] bytes = stream.toByteArray();
                    Glide.with(getApplicationContext())
                            .load(img)
                            .centerCrop()
                            .into(btn_diary_photo);

                    if (diaryRecord == null) {
                        Diary insertRecord = new Diary();
                        insertRecord.setDate(dbFormat.format(d));
                        insertRecord.setText(et_diary.getText().toString());
                        insertRecord.setImage(bytes);
                        insertRecord.setImageNullCheck(1);

                        mDiaryDao.insertDiary(insertRecord);
                    } else { // 기존 레코드 존재
                        Diary updateRecord = new Diary();
                        updateRecord.setDate(dbFormat.format(d));
                        updateRecord.setText(et_diary.getText().toString());
                        updateRecord.setImage(bytes);
                        updateRecord.setImageNullCheck(1);

                        mDiaryDao.updateDiary(updateRecord);
                    }

                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * ImageView -> bitmap -> byte array 추출
     *
     * @param image ImageView
     * @return byte array
     */
    private byte[] getImageInByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageInByte = baos.toByteArray();
        return imageInByte;
    }

    /**
     * convert from byte array to bitmap
     *
     * @param image byte array
     * @return bitmap image
     */
    public static Bitmap getImageInBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * 해당 날짜에 레코드가 존재하는지 확인
     *
     * @return Diary (object)
     */
    public Diary isExist() {
        Diary diary = mDiaryDao.findByDate(dbFormat.format(d));
        return diary;
    }

    /**
     * 데이터베이스 삽입, 수정 메소드
     */
    private void insertRecord() {
        Diary insertRecord = new Diary();
        insertRecord.setDate(dbFormat.format(d));
        insertRecord.setText(et_diary.getText().toString());
        if (imageNullCheck == 1) {
            insertRecord.setImage(getImageInByte(btn_diary_photo));
        }
        mDiaryDao.insertDiary(insertRecord);
    }

    private void updateRecord() {
        Diary updateRecord = new Diary();
        updateRecord.setDate(dbFormat.format(d));
        updateRecord.setText(et_diary.getText().toString());
        if (imageNullCheck == 1) {
            updateRecord.setImage(getImageInByte(btn_diary_photo));
        }
        mDiaryDao.updateDiary(updateRecord);
    }
}
