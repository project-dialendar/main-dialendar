package com.example.main_dialendar.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.util.Log;
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
import com.example.main_dialendar.view.dialog.CaptureDialog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryActivity extends AppCompatActivity {
    private static final String TAG = "DiaryActivity";

    /* 위젯 */
    private ImageView btn_diary_options;
    private ImageView iv_photo;
    private ImageView btn_save_back;
    private TextView tv_diary_date;
    private EditText et_diary;

    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd.");
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");

    /* 데이터베이스 */
    private DiaryDao mDiaryDao;
    DiaryDatabase database;

    /* 이미지 */
    private static final int REQUEST_CODE = 0;
    Bitmap img = null;
    Bitmap resizedBitmapImg = null;
    Boolean isImgUpdated = false;
    byte[] imgInByte = null;

    Date date = new Date();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        /* 데이터베이스 생성 */
        database = DiaryDatabase.getInstance(this);
        mDiaryDao = database.diaryDao();

        btn_diary_options = findViewById(R.id.btn_diary_options);
        iv_photo = findViewById(R.id.iv_photo);

        btn_save_back = findViewById(R.id.btn_save_back);
        tv_diary_date = findViewById(R.id.tv_diary_date);
        et_diary = findViewById(R.id.et_diary);

        /* 일기 날짜 세팅 */
        Intent diaryIntent = getIntent();
        boolean isToday = diaryIntent.getBooleanExtra("today", true);
        if (isToday)
            tv_diary_date.setText(getTime());
        else {
            String tv_date = diaryIntent.getStringExtra("date");
            tv_diary_date.setText(tv_date);
            try {
                date = mFormat.parse(tv_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /* 일기 레코드 불러오기 */
        Diary diaryRecord = isExist();
        try {
            if (diaryRecord.getText() != null) {
                et_diary.setText(diaryRecord.getText());
            }
            if (diaryRecord.getImage() != null) {
                img = getBitmapInByte(diaryRecord.getImage());
                iv_photo.setImageBitmap(img);
            }
        } catch (NullPointerException e) {
        }

        iv_photo.setOnClickListener(v -> pickFromGallery());

        btn_save_back.setOnClickListener(v -> {
            if (diaryRecord == null) {
                insertRecord();
            } else {
                updateRecord();
            }
            finish();
        });

        btn_diary_options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(DiaryActivity.this, btn_diary_options);

            popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.share_diary:
                        CaptureDialog captureDialog = new CaptureDialog(DiaryActivity.this);

                        Diary diaryRecord1 = isExist();
                        if (diaryRecord1 == null) { // 날짜에 일기가 없음
                            Toast.makeText(
                                    DiaryActivity.this,
                                    getString(R.string.diary_not_exist),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if (diaryRecord1.getText().matches("")) {
                                if (diaryRecord1.getImage() == null) { // 일기 내용, 사진 없음
                                    Toast.makeText(
                                            DiaryActivity.this,
                                            getString(R.string.diary_not_exist),
                                            Toast.LENGTH_LONG).show();
                                }else { // 사진만 있음
                                    captureDialog.callCaptureDialog(
                                            mFormat.format(date),
                                            getBitmapInByte(diaryRecord1.getImage()),
                                            null
                                    );
                                }
                            } else {
                                if (diaryRecord1.getImage() == null) { // 내용만 있음
                                    captureDialog.callCaptureDialog(
                                            mFormat.format(date),
                                            null,
                                            diaryRecord1.getText()
                                    );
                                }else { // 일기 내용, 사진 다 있음
                                    captureDialog.callCaptureDialog(
                                            mFormat.format(date),
                                            getBitmapInByte(diaryRecord1.getImage()),
                                            diaryRecord1.getText());
                                }
                            }
                        }
                        break;
                    case R.id.delete_diary:
                        Diary deleteRecord = new Diary();
                        deleteRecord.setDate(dbFormat.format(date));

                        mDiaryDao.deleteDiary(deleteRecord);

                        finish();
                }
                return true;
            });
            popupMenu.show();
        });

        iv_photo.setBackground(getResources().getDrawable(R.drawable.round_image_border, null));
        iv_photo.setClipToOutline(true);
    }


    /**
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
     * 현재 시간 반환 메소드
     */
    private String getTime() {
        long now = System.currentTimeMillis();
        date = new Date(now);
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
    /**
     *  이미지뷰에 이미지 비트맵으로 넣기
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    img = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    Glide.with(getApplicationContext())
                            .load(img)
                            .centerCrop()
                            .into(iv_photo);
                    iv_photo.setBackground(getResources().getDrawable(R.drawable.round_image_border, null));
                    iv_photo.setClipToOutline(true);

                    isImgUpdated = true;
                    // 이미지 저장할 준비
                    imgInByte = getByteInBitmap(img);
                    while (imgInByte.length > 500000) {
                        Log.e(TAG, "이미지가 너무 큽니다 | "+imgInByte.length);
                        Log.e(TAG, "width: " + img.getWidth() + " height: " + img.getHeight());
                        img = Bitmap.createScaledBitmap(img, (int) (img.getWidth() * 0.8), (int) (img.getHeight() * 0.8), true);
                        imgInByte = getByteInBitmap(img);
                    }

                    Log.e(TAG, "최종 이미지 사이즈 | "+imgInByte.length);
                    Log.e(TAG, "최종 width: " + img.getWidth() + " height: " + img.getHeight());
                } catch (Exception e) {
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * convert from bitmap to byte array
     *
     * @param bitmap Bitmap
     * @return byte array
     */
    private byte[] getByteInBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * convert from byte array to bitmap
     *
     * @param image byte array
     * @return bitmap
     */
    public static Bitmap getBitmapInByte(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * 해당 날짜에 레코드가 존재하는지 확인
     */
    public Diary isExist() {
        return mDiaryDao.findByDate(dbFormat.format(date));
    }

    /**
     * 데이터베이스 삽입, 수정 메소드
     */
    private void insertRecord() {
        Diary insertRecord = new Diary();
        insertRecord.setDate(dbFormat.format(date));
        insertRecord.setText(et_diary.getText().toString());
        if (img == null)
            insertRecord.setImage(null);
        else{
            insertRecord.setImage(imgInByte);
        }
        mDiaryDao.insertDiary(insertRecord);
        Log.e(TAG, "레코드 등록");
    }
    private void updateRecord() {
        if (iv_photo.getDrawable() == null || !isImgUpdated) {
            mDiaryDao.updateExceptImage(
                    dbFormat.format(date),
                    et_diary.getText().toString());
            Log.e(TAG, "이미지 미포함 업데이트");
        } else { // 이미지 변경시에만 이미지 업데이트
            Diary updateRecord = new Diary();
            updateRecord.setDate(dbFormat.format(date));
            updateRecord.setText(et_diary.getText().toString());
            updateRecord.setImage(imgInByte);
            mDiaryDao.updateDiary(updateRecord);
            Log.e(TAG, "이미지 포함 업데이트");
        }
    }
}
