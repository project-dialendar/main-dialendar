package com.example.main_dialendar.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main_dialendar.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryActivity extends AppCompatActivity {

    private ImageView btn_diary_options, btn_diary_photo, btn_save_back;
    private TextView tv_diary_date;
    private EditText et_diary;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd. hh:mm");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        btn_diary_options = findViewById(R.id.btn_diary_options);
        btn_diary_photo = findViewById(R.id.btn_diary_photo);
        btn_save_back = findViewById(R.id.btn_save_back);

        tv_diary_date = findViewById(R.id.tv_diary_date);
        et_diary = findViewById(R.id.et_diary);

        Intent diaryIntent = getIntent();
        boolean isToday = diaryIntent.getBooleanExtra("today", true);
        if (isToday)
            tv_diary_date.setText(getTime());

        // 달력 화면으로 나가면서 저장
        btn_save_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        return mFormat.format(date);
    }
}
