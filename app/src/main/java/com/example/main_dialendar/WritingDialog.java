package com.example.main_dialendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WritingDialog extends AppCompatActivity {

    private TextView tv_date;
    private ImageButton btn_options, btn_photo;
    private EditText et_writing;


    private static final int PICK_IMAGE = 1111;
    private Context context;

    public WritingDialog(Context context) {
        this.context = context;


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_writing);

        // 커스텀 다이얼로그를 정의하기위해 dialog 클래스 생성
        final Dialog dialog = new Dialog(context);

        // 액티비티의 타이틀바 숨김
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그 레이아웃 설정
        dialog.setContentView(R.layout.dialog_writing);

        // 커스텀 다이얼로그 노출
        dialog.show();

        // 커스텀 다이얼로그의 각 위젯 정의
        tv_date = findViewById(R.id.tv_date_writing);
        btn_options = findViewById(R.id.btn_writing_options);
        btn_photo = findViewById(R.id.btn_photo);
        et_writing = findViewById(R.id.et_writing_text);


        // 이미지 버튼 -> 갤러리에서 사진 불러오기
        btn_photo.setOnClickListener(v -> pickFromGallery());

        // 날짜 설정 <- 메인에서 날짜 정보 가져오기
        Calendar cal = Calendar.getInstance();
        Date nowDate = cal.getTime();
        SimpleDateFormat dataformat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String toDay = dataformat.format(nowDate);

        tv_date.setText((CharSequence) toDay);

        // 텍스트 저장 -> 메인으로 텍스트 넘겨서 저장?


        // 옵션 1.일기삭제 2. 공유


    }
/*
    // 호출할 디이얼로그 함수
    public void callFunction(){


    }
*/
    // 사진 불러오는 메서드 (내장인텐트사용)
    private void pickFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Uri selectedImage = data.getData();
            btn_photo.setImageURI(selectedImage);
        }
    }

}
