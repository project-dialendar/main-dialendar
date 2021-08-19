package com.example.main_dialendar.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.main_dialendar.R;

/**
 * 설정 화면 Activity
 */
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 상단 툴바 설정
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowCustomEnabled(true);    // 커스터마이징을 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // 툴바 메뉴 버튼 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back); // 메뉴 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    // 툴바에 있는 옵션을 선택한 경우
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                // 뒤로 가기
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}