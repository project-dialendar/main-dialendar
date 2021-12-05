package com.example.main_dialendar.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.main_dialendar.R;
import com.example.main_dialendar.util.setting.SharedPrefManager;

import java.util.Stack;

public class LockActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_0;
    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button btn_4;
    Button btn_5;
    Button btn_6;
    Button btn_7;
    Button btn_8;
    Button btn_9;
    Button btn_delete;

    TextView tv_password;

    SharedPrefManager mSharedPref;

    int cnt = 0;
    Stack<Integer> pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        mSharedPref = SharedPrefManager.getInstance(getApplicationContext());

        btn_0 = findViewById(R.id.btn_0);
        btn_0.setOnClickListener(this);

        btn_1 = findViewById(R.id.btn_1);
        btn_1.setOnClickListener(this);

        btn_2 = findViewById(R.id.btn_2);
        btn_2.setOnClickListener(this);

        btn_3 = findViewById(R.id.btn_3);
        btn_3.setOnClickListener(this);

        btn_4 = findViewById(R.id.btn_4);
        btn_4.setOnClickListener(this);

        btn_5 = findViewById(R.id.btn_5);
        btn_5.setOnClickListener(this);

        btn_6 = findViewById(R.id.btn_6);
        btn_6.setOnClickListener(this);

        btn_7 = findViewById(R.id.btn_7);
        btn_7.setOnClickListener(this);

        btn_8 = findViewById(R.id.btn_8);
        btn_8.setOnClickListener(this);

        btn_9 = findViewById(R.id.btn_9);
        btn_9.setOnClickListener(this);

        btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);

        tv_password = findViewById(R.id.tv_password);

        if (mSharedPref.getLockOff()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }


        int flag = getIntent().getIntExtra("lock", 1);
        if (flag == 0) {
            pw = new Stack<>();
            if (cnt == 4) {
                // pw 저장
                int num = 0;
                for (int i = 0; i < 4; i++) num += pw.pop() * Math.pow(10, i);
                mSharedPref.setPassword(num);

                // 화면 전환 (초기화)
                tv_password.setText("한번 더 입력해주세요!");
                cnt = 0;
                flag = 1;

            }
        } else if (flag == 1) {
            if (cnt == 4) {
                int num = 0;
                for (int i = 0; i < 4; i++) num += pw.pop() * Math.pow(10, i);
                if (num == mSharedPref.getPassword()) {
                    // pw 설정 완료
                    // 되돌아가기
                    finish();
                }
            } else {
                // 일반 잠금 모드
                if (cnt == 4) {
                    int num = 0;
                    for (int i = 0; i < 4; i++) num += pw.pop() * Math.pow(10, i);
                    if (num == mSharedPref.getPassword()) {
                        startActivity(new Intent(LockActivity.this, MainActivity.class));
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        if (view == btn_delete && cnt > 0) {
            cnt--;
            pw.pop();
        }
        else if (cnt < 4) {
            cnt++;
            pw.add(Integer.parseInt(btn.getText().toString()));
        }
    }
}