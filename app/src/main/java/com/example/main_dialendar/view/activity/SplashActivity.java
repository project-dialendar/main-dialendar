package com.example.main_dialendar.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.main_dialendar.util.lock.ScreenService;
import com.example.main_dialendar.util.setting.SharedPrefManager;
import com.example.main_dialendar.view.activity.MainActivity;

/* 스플래시 화면, 수정날짜: 2021-06-24, 장현애 */
public class SplashActivity extends AppCompatActivity {

    SharedPrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = SharedPrefManager.getInstance(getApplicationContext());

        Log.e("LOCK", prefManager.getLockOff()+"");
        if (prefManager.getLockOff())
            startService(new Intent(getApplicationContext(), ScreenService.class));

        // 메인 화면으로 이동
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
