package com.apps.main_dialendar.util.message;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

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
