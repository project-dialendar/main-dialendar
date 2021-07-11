package com.example.main_dialendar.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.main_dialendar.view.activity.MainActivity;

/* 스플래시 화면, 수정날짜: 2021-06-24, 장현애 */
public class SplashActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
