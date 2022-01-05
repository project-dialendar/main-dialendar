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

    private static String CHANNEL_ID = "Channel1";
    private static String CHANNEL_NAME = "Channel1";

    public MessageReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();

        int id = (int) System.currentTimeMillis();
        Notification notification = createNotification();
        manager.notify(id, notification);
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
    }

    private Notification createNotification() {
        builder = null;
        builder.setContentTitle("일력");
        builder.setSmallIcon(R.drawable.img_logo);
        builder.setAutoCancel(true);

        Intent intentToMain = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 101, intentToMain, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        return builder.build();
    }
}
