package com.example.main_dialendar.util.message;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.main_dialendar.R;
import com.example.main_dialendar.view.activity.MainActivity;

// 노티 메시지 수신 및 전송
public class MessageReceiver extends BroadcastReceiver {

    NotificationManager manager;
    NotificationCompat.Builder builder;
    Context context;

    public MessageReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        MessageHelper helper = new MessageHelper(context);
        NotificationCompat.Builder builder = helper.createNotification();
        helper.getManager().notify(1, builder.build());
    }
}
