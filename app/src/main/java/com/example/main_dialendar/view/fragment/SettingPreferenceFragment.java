package com.example.main_dialendar.view.fragment;

import static android.app.Activity.*;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.main_dialendar.R;
import com.example.main_dialendar.util.message.MessageReceiver;
import com.example.main_dialendar.util.setting.SharedPrefManager;
import com.example.main_dialendar.util.theme.ThemeUtil;
import com.example.main_dialendar.view.activity.SettingActivity;
import com.example.main_dialendar.view.dialog.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 설정 목록을 보여주는 프레그먼트
 */
public class SettingPreferenceFragment extends PreferenceFragmentCompat {

    static SharedPreferences localPref;
    SharedPrefManager sharedPref;

    SwitchPreferenceCompat messagePref;
    SwitchPreferenceCompat lockPref;
    ListPreference darkmodePref;

    private static final int LOCKMODE_ON = 10000;
    private static final int LOCKMODE_OFF = 9999;

    int messageHour, messageMinute;
    SimpleDateFormat messageFormat = new SimpleDateFormat("a hh:mm");

    String themeColor;

    Context context;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey);  // xml 파일과 연동
        context = getActivity();

        localPref = PreferenceManager.getDefaultSharedPreferences(context);

        messagePref = findPreference("message");
        messagePref.setOnPreferenceChangeListener(onPreferenceChangeListener);
        lockPref = findPreference("lock");
        lockPref.setOnPreferenceChangeListener(onPreferenceChangeListener);
        darkmodePref = findPreference("darkmode");
        darkmodePref.setOnPreferenceChangeListener(onPreferenceChangeListener);

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

    Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            if (key.equals("message")) setMessage();
            if (key.equals("darkmode")) setDarkmode();
            if (key.equals("lock")) setLockmode();

            return false;
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
        ThemeUtil.modSave(context, themeColor);

        darkmodePref.setSummary(localPref.getString("darkmode", "Default"));
        sharedPref.setDarkmode(localPref.getString("darkmode", "Default"));
    }

    private void setLockmode() {
        SettingActivity settingActivity = (SettingActivity) getActivity();
        if (localPref.getBoolean("lock", false))
            settingActivity.moveToLockActivity(LOCKMODE_ON);
        else
            settingActivity.moveToLockActivity(LOCKMODE_OFF);
    }

    private void showMessageDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setListener(timeSetListener);
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.setCancelable(true);
        timePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("###", "Fragment: onActivityResult!!");

        switch(data.getIntExtra("mode", LOCKMODE_OFF)) {
            case LOCKMODE_ON:
                if (resultCode == RESULT_OK) {
                    lockPref.setSummary("사용");
                    sharedPref.setLockOn(true);
                }
                else
                    lockPref.setChecked(false);
                break;
            case LOCKMODE_OFF:
                if (resultCode == RESULT_OK) {
                    lockPref.setSummary("사용 안 함");
                    sharedPref.setLockOn(false);
                }
                else
                    lockPref.setChecked(true);
                break;
        }
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
