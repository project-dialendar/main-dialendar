package com.example.main_dialendar.view.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.BaseAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.main_dialendar.R;
import com.example.main_dialendar.util.message.MessageReceiver;
import com.example.main_dialendar.util.setting.SharedPrefManager;
import com.example.main_dialendar.util.theme.ThemeUtil;
import com.example.main_dialendar.view.activity.LockActivity;
import com.example.main_dialendar.view.activity.SettingActivity;
import com.example.main_dialendar.view.dialog.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 설정 목록을 보여주는 프레그먼트
 */
public class SettingPreferenceFragment extends PreferenceFragment {

    static SharedPreferences localPref;
    SharedPrefManager sharedPref;

    SwitchPreference messagePref;
    SwitchPreference lockPref;
    ListPreference darkmodePref;

    private static final int LOCKMODE_ON = 10000;
    private static final int LOCKMODE_OFF = 9999;

    int messageHour, messageMinute;
    SimpleDateFormat messageFormat = new SimpleDateFormat("a hh:mm");

    String themeColor;

    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);  // xml 파일과 연동

        context = getActivity();

        localPref = PreferenceManager.getDefaultSharedPreferences(context);
        localPref.registerOnSharedPreferenceChangeListener(prefListener);

        messagePref = (SwitchPreference)findPreference("message");
        lockPref = (SwitchPreference)findPreference("lock");
        darkmodePref = (ListPreference)findPreference("darkmode");

        // sharedPreferences 연결
        sharedPref = SharedPrefManager.getInstance(context);
        messageHour = sharedPref.getMessageHour();
        messageMinute = sharedPref.getMessageMinute();

        // 디폴트 값 설정
        setDefaultInPrefs();
    }

    private void setDefaultInPrefs() {
        if(!localPref.getBoolean("message", false))
            messagePref.setSummary("사용 안 함");
        else
            messagePref.setSummary(getMessageText(getMessageTime()));

        if(!localPref.getBoolean("lock", false))
            lockPref.setSummary("사용 안 함");
        else
            lockPref.setSummary("사용");

        darkmodePref.setSummary(localPref.getString("darkmode", "Default"));
    }

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals("message")) setMessage();
            if (key.equals("darkmode")) setDarkmode();
            if (key.equals("lock")) setLockmode();

            // 2 deqth PreferenceScreen 내부에서 발생한 설정 내용을 적용시키기 위함
            ((BaseAdapter) getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
        }
    };

    private void setMessage() {
        if (localPref.getBoolean("message", false))
            showMessageDialog();
        else {
            cancelAlarmManager();
            messagePref.setSummary("사용 안 함");
            sharedPref.setMessageOn(false);
        }
    }

    private void setDarkmode() {
        if (localPref.getString("darkmode", "Default").equals("Dark"))
            themeColor = ThemeUtil.DARK_MODE;
        else if (localPref.getString("darkmode", "Default").equals("Light"))
            themeColor = ThemeUtil.LIGHT_MODE;
        else
            themeColor = ThemeUtil.DEFAULT_MODE;

        ThemeUtil.applyTheme(themeColor);
        ThemeUtil.modSave(SettingActivity.context, themeColor);

        darkmodePref.setSummary(localPref.getString("darkmode", "Default"));
        sharedPref.setDarkmode(localPref.getString("darkmode", "Default"));
    }

    private void setLockmode() {
        if (localPref.getBoolean("lock", false)) {
            moveToLockActivity(LOCKMODE_ON);
            lockPref.setSummary("사용");
            sharedPref.setLockOn(true);
        }
        else{
            moveToLockActivity(LOCKMODE_OFF);
            lockPref.setSummary("사용 안 함");
            sharedPref.setLockOn(false);
        }
    }

    private void showMessageDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setListener(timeSetListener);
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.setCancelable(true);
        timePickerDialog.show();
    }

    private void moveToLockActivity(int mode) {
        Intent intent = new Intent(context, LockActivity.class);
        intent.putExtra("lock", mode);
        startActivity(intent);
    }

    android.app.TimePickerDialog.OnTimeSetListener timeSetListener = new android.app.TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            // save message's hour and minute
            messageHour = hour;
            messageMinute = minute;

            updateMessageText();
            startAlarmManager(getMessageTime());
        }
    };

    private void updateMessageText() {
        messagePref.setSummary(getMessageText(getMessageTime()));
        sharedPref.setMessageOn(true, messageHour, messageMinute);
    }

    private Calendar getMessageTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, messageHour);
        cal.set(Calendar.MINUTE, messageMinute);

        if (cal.before(Calendar.getInstance()))
            cal.add(Calendar.DATE, 1);
        return cal;
    }

    private void startAlarmManager(Calendar cal) {
        Intent intent = new Intent(context, MessageReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(context, "알림이 설정되었습니다.", Toast.LENGTH_LONG).show();
    }

    private void cancelAlarmManager() {
        Intent intent = new Intent(context, MessageReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private String getMessageText(Calendar cal) {
        return messageFormat.format(new Date(cal.getTimeInMillis()));
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof Activity) this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        context = null;
        super.onDetach();
    }
}
