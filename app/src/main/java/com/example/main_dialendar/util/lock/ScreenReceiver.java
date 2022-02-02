package com.example.main_dialendar.util.lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.main_dialendar.view.activity.PasswordSettingActivity;

// 화면이 꺼졌을 때를 감지하고 LockActivity를 실행
public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Intent i = new Intent(context, PasswordSettingActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
