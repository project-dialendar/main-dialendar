package com.apps.main_dialendar.view.fragment;

import static android.app.Activity.*;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.apps.main_dialendar.R;
import com.apps.main_dialendar.util.message.MessageReceiver;
import com.apps.main_dialendar.util.setting.SharedPrefManager;
import com.apps.main_dialendar.view.activity.PasswordSettingActivity;
import com.apps.main_dialendar.view.activity.LockActivity;
import com.apps.main_dialendar.view.dialog.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 설정 목록을 보여주는 프레그먼트
 */
public class SettingPreferenceFragment extends PreferenceFragmentCompat {

    // preference key
    public static final String MESSAGE = "message";
    public static final String LOCK = "lock";
    public static final String DARKMODE = "darkmode";

    private SharedPrefManager sharedPref;
    private SwitchPreferenceCompat messagePref;
    private SwitchPreferenceCompat lockPref;
    private ListPreference darkmodePref;

    private static final int LOCKMODE_ON = 10000;
    private static final int LOCKMODE_OFF = 9999;

    private int messageHour, messageMinute;
    private SimpleDateFormat messageFormat = new SimpleDateFormat("a hh:mm");

    private Context context;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey);  // xml 파일과 연동
        context = getActivity();

        connectWithSharedPreferences();
        setLocalPreferences();
    }

    // sharedPref와 연결 & 값 불러오기
    private void connectWithSharedPreferences() {
        sharedPref = SharedPrefManager.getInstance(context);
        messageHour = sharedPref.getMessageHour();
        messageMinute = sharedPref.getMessageMinute();
    }

    // 프레그먼트 뷰와 preference 연동
    private void setLocalPreferences() {
        messagePref = findPreference(MESSAGE);
        messagePref.setOnPreferenceChangeListener(onPreferenceChangeListener);
        lockPref = findPreference(LOCK);
        lockPref.setOnPreferenceChangeListener(onPreferenceChangeListener);
        darkmodePref = findPreference(DARKMODE);
        darkmodePref.setOnPreferenceChangeListener(onPreferenceChangeListener);

        // 디폴트 값 설정
        setDefaultInPrefs();
    }

    private void setDefaultInPrefs() {
        if(sharedPref.getMessageOff())
            messagePref.setSummary(context.getString(R.string.setting_true));

        if(sharedPref.getLockOff())
            lockPref.setSummary(context.getString(R.string.setting_true));

        darkmodePref.setSummary(sharedPref.getDarkmode());
    }

    // 값이 변경될 때마다 리스너 실행
    Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            var key = preference.getKey();
            if (key.equals(MESSAGE))
                setMessage();
            else if (key.equals(LOCK))
                setLockmode();
            else if (key.equals(DARKMODE))
                setDarkmode((String) newValue);
            return false;
        }
    };

    // 알림 설정
    private void setMessage() {
        if (!sharedPref.getMessageOff()) {
            showMessageDialog();    // 알림 설정 ON -> 시간 설정 다이얼로그 띄우기
        } else {
            cancelAlarmManager();
            updateMessageTextOff();
        }
    }

    // 다크모드 설정
    private void setDarkmode(String newMode) {
        sharedPref.applyTheme(newMode);
        sharedPref.setDarkmode(newMode);

        updateDarkmodeText(newMode);
    }

    // 잠금모드 설정
    private void setLockmode() {
        if (!sharedPref.getLockOff())
            moveToLockActivity(LOCKMODE_ON);
        else
            moveToLockActivity(LOCKMODE_OFF);
    }

    public void moveToLockActivity(int mode) {
        Intent intent;
        switch (mode) {
            case LOCKMODE_ON:
                intent = new Intent(context, PasswordSettingActivity.class);
                lockOnLauncher.launch(intent);
                break;
            case LOCKMODE_OFF:
                intent = new Intent(context, LockActivity.class);
                intent.putExtra("mode", LOCKMODE_OFF);
                lockOffLauncher.launch(intent);
                break;
        }
    }

    ActivityResultLauncher<Intent> lockOnLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            int resultCode = result.getResultCode();
            switch(resultCode) {
                case RESULT_OK:
                    lockPref.setSummary(getResources().getString(R.string.setting_true));
                    lockPref.setChecked(true);
                    sharedPref.setLockOn(true);
                    break;
                case RESULT_CANCELED:
                    lockPref.setSummary(getResources().getString(R.string.setting_false));
                    lockPref.setChecked(false);
                    break;
            }
        }
    });

    ActivityResultLauncher<Intent> lockOffLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();
                    switch(resultCode) {
                        case RESULT_OK:
                            lockPref.setSummary(getResources().getString(R.string.setting_false));
                            lockPref.setChecked(false);
                            sharedPref.setLockOn(false);
                            break;
                        case RESULT_CANCELED:
                            lockPref.setSummary(getResources().getString(R.string.setting_true));
                            lockPref.setChecked(true);
                            break;
                    }
                }
            });

    private void showMessageDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setListener(timeSetListener);
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.setCancelable(true);
        timePickerDialog.show();
    }

    android.app.TimePickerDialog.OnTimeSetListener timeSetListener = new android.app.TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            // save message's hour and minute
            messageHour = hour;
            messageMinute = minute;

            updateMessageTextOn();
            startAlarmManager(getMessageTime());
        }
    };

    private void startAlarmManager(Calendar cal) {
        Intent intent = new Intent(context, MessageReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(context, context.getString(R.string.setting_message), Toast.LENGTH_LONG).show();
    }

    private void cancelAlarmManager() {
        Intent intent = new Intent(context, MessageReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void updateMessageTextOn() {
        messagePref.setSummary(getMessageText(getMessageTime()));
        messagePref.setChecked(true);
        sharedPref.setMessageOn(true, messageHour, messageMinute);
    }

    private void updateMessageTextOff() {
        messagePref.setSummary(context.getString(R.string.setting_false));
        messagePref.setChecked(false);
        sharedPref.setMessageOn(false);
    }

    private void updateDarkmodeText(String newMode) {
        darkmodePref.setSummary(newMode);
        darkmodePref.setValue(newMode);
        sharedPref.setDarkmode(newMode);
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
