package com.apps.main_dialendar.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.main_dialendar.R;
import com.apps.main_dialendar.util.lock.ScreenService;
import com.apps.main_dialendar.util.setting.SharedPrefManager;

import java.util.Stack;

public class PasswordSettingActivity extends AppCompatActivity implements View.OnClickListener {

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
    Button btn_cancel;

    ImageView iv_password1;
    ImageView iv_password2;
    ImageView iv_password3;
    ImageView iv_password4;

    TextView tv_password;

    SharedPrefManager mSharedPref;

    int flag = SETTING_FIRST, cnt = 0;
    Stack<Integer> pw;

    private static final int SETTING_FIRST = 10000;
    private static final int SETTING_SECOND = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting);

        // 변수 설정
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

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);

        iv_password1 = findViewById(R.id.iv_password1);
        iv_password2 = findViewById(R.id.iv_password2);
        iv_password3 = findViewById(R.id.iv_password3);
        iv_password4 = findViewById(R.id.iv_password4);

        tv_password = findViewById(R.id.tv_password);

        pw = new Stack<>();
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;

        if (btn == btn_cancel) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        else if (btn == btn_delete) {
            if (cnt > 0) deletePassword();
        }
        else if (cnt < 4) {
            int input = Integer.parseInt(btn.getText().toString());
            addPassword(input);
        }

        changeIvPassword();
    }

    private void addPassword(int num) {
        cnt++;
        pw.add(num);

        if (cnt == 4) checkPassword();
    }

    private void deletePassword() {
        cnt--;
        pw.pop();
    }

    // 비밀번호 확인 절차
    private void checkPassword() {
        int input = 0;
        for (int i = 0; i < 4; i++) input += pw.pop() * Math.pow(10, i);

        switch(flag) {
            case SETTING_FIRST:
                readFirstPassword(input);
                break;
            case SETTING_SECOND:
                readSecondPassword(input);
                break;
        }
    }

    private void readFirstPassword(int input) {
        mSharedPref.setPassword(input);

        tv_password.setText("한번 더 입력해주세요!");
        cnt = 0;
        flag = SETTING_SECOND;
    }

    private void readSecondPassword(int input) {
        if (input == mSharedPref.getPassword()) {
            Toast.makeText(getApplicationContext(), "비밀번호 설정을 완료했습니다.", Toast.LENGTH_LONG).show();
            setLockmodeOn();

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
        else failToOpen();
    }

    private void setLockmodeOn() {
        Intent lockService = new Intent(getApplicationContext(), ScreenService.class);
        startService(lockService);
    }

    private void failToOpen() {
        Toast.makeText(getApplicationContext(), "비밀번호가 틀렸습니다.", Toast.LENGTH_LONG).show();
        cnt = 0;
    }

    // 비밀번호 숫자 입력할 때마다 이미지뷰 변경
    private void changeIvPassword() {
        switch (cnt) {
            case 0:
                iv_password1.setImageResource(R.drawable.ic_baseline_password_blank);
                iv_password2.setImageResource(R.drawable.ic_baseline_password_blank);
                iv_password3.setImageResource(R.drawable.ic_baseline_password_blank);
                iv_password4.setImageResource(R.drawable.ic_baseline_password_blank);
                break;
            case 1:
                iv_password1.setImageResource(R.drawable.ic_baseline_password_fill);
                iv_password2.setImageResource(R.drawable.ic_baseline_password_blank);
                break;
            case 2:
                iv_password2.setImageResource(R.drawable.ic_baseline_password_fill);
                iv_password3.setImageResource(R.drawable.ic_baseline_password_blank);
                break;
            case 3:
                iv_password3.setImageResource(R.drawable.ic_baseline_password_fill);
                iv_password4.setImageResource(R.drawable.ic_baseline_password_blank);
                break;
            case 4:
                iv_password4.setImageResource(R.drawable.ic_baseline_password_fill);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();

        super.onBackPressed();
    }
}