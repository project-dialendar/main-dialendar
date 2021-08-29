package com.example.main_dialendar.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main_dialendar.R;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WritingDialog extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private TextView tv_date;
    private ImageButton btn_options, btn_photo;
    private EditText et_writing;


    private static final int PICK_IMAGE = 1111;
    private Context context;

    public WritingDialog(Context context) {
        this.context = context;


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_writing);

        // 커스텀 다이얼로그를 정의하기위해 dialog 클래스 생성
        final Dialog dialog = new Dialog(context);

        // 액티비티의 타이틀바 숨김
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

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
        LocalDate currentDate = LocalDate.now();    // 컴퓨터의 현재 날짜 정보를 저장한 LocalDate 객체를 리턴한다. 결과 : 2016-04-01
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분");
        String nowString = currentDate.format(dateTimeFormatter);   // 결과 : 2016년 4월 2일 오전 1시 4분

        tv_date.setText(nowString);

        // 텍스트 저장 -> 메인으로 텍스트 넘겨서 저장?


        // 옵션 1.일기삭제 2. 공유
        btn_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(WritingDialog.this, btn_options);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.share_diary:
                            case R.id.delete_diary:
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

    }

    // 사진 불러오는 메서드 (내장인텐트사용)
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

                    btn_photo.setImageBitmap(img);
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }
}
