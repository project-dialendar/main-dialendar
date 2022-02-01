package com.example.main_dialendar.view.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.main_dialendar.R;
import com.example.main_dialendar.view.fragment.SettingPreferenceFragment;

/**
 * 설정 화면 Activity
 */
public class SettingActivity extends AppCompatActivity {

    // 뒤로가기 버튼
    Button btn_back;

//    private ActivityResultLauncher<Intent> lockOnOffLauncher;


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
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
                finish();
            }
        });

//        lockOnOffLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        Log.e("###", "Activity에서 실행");
//                    }
//                }
//        );
    }
}