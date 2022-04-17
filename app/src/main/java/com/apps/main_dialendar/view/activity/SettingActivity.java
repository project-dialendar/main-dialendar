package com.apps.main_dialendar.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.apps.main_dialendar.R;
import com.apps.main_dialendar.view.fragment.SettingPreferenceFragment;

/**
 * 설정 화면 Activity
 */
public class SettingActivity extends AppCompatActivity {

    // 뒤로가기 버튼
    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_fragment, new SettingPreferenceFragment())
                .commit();

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.super.onBackPressed();
            }
        });

    }
}