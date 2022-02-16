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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.main_dialendar.R;
import com.example.main_dialendar.util.message.MessageReceiver;
import com.example.main_dialendar.util.setting.SharedPrefManager;
import com.example.main_dialendar.util.theme.ThemeUtil;
import com.example.main_dialendar.view.activity.PasswordSettingActivity;
import com.example.main_dialendar.view.activity.LockActivity;
import com.example.main_dialendar.view.activity.SettingActivity;
import com.example.main_dialendar.view.dialog.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 설정 목록을 보여주는 프레그먼트
 */
public class SettingPreferenceFragment extends PreferenceFragmentCompat {

    public static SharedPreferences localPrefs;

    public static final String MESSAGE = "message";
    public static final String LOCK = "lock";
    public static final String DARKMODE = "darkmode";
    private static final String LIGHT = "Light";
    private static final String DARK = "Dark";
    private static final String DEFAULT = "Default";

    private static final int LOCKMODE_ON = 10000;
    private static final int LOCKMODE_OFF = 99999;

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
      
        setLocalPreferences();
        connectWithSharedPreferences();
    }

    private void setLocalPreferences() {
        localPref = PreferenceManager.getDefaultSharedPreferences(context);
        messagePref = findPreference("message");
        messagePref.setOnPreferenceChangeListener(onPreferenceChangeListener);
        lockPref = findPreference("lock");
        lockPref.setOnPreferenceChangeListener(onPreferenceChangeListener);
        darkmodePref = findPreference("darkmode");
        darkmodePref.setOnPreferenceChangeListener(onPreferenceChangeListener);

        // 디폴트 값 설정
        setDefaultInPrefs();
    }

    private void connectWithSharedPreferences() {
        sharedPref = SharedPrefManager.getInstance(context);
        messageHour = sharedPref.getMessageHour();
        messageMinute = sharedPref.getMessageMinute();
    }

    private void setDefaultInPrefs() {
        if(!localPrefs.getBoolean(MESSAGE, false))
            messagePreference.setSummary(context.getString(R.string.setting_true));

        if(localPrefs.getBoolean(LOCK, false))
            lockPreference.setSummary(context.getString(R.string.setting_true));

        if(!localPrefs.getString(DARKMODE, "").equals(""))
            darkmodePreference.setSummary(localPrefs.getString(DARKMODE, DEFAULT));
    }

    Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals(MESSAGE)) setMessage();
            if (key.equals(DARKMODE)) setDarkmode();
            if (key.equals(LOCK)) setLockmode();
            return false;
        }
    };

    private void setMessage() {
        if (localPrefs.getBoolean(MESSAGE, false)) {
            messagePreference.setSummary(context.getString(R.string.setting_true));
            showMessageDialog();
        else {
            messagePreference.setSummary(context.getString(R.string.setting_false));
            prefManager.setMessageOn(false);
        }
    }

    private void setDarkmode() {
        if (localPrefs.getString(DARKMODE, DEFAULT).equals(DARK))
            themeColor = ThemeUtil.DARK_MODE;
        else if (localPrefs.getString(DARKMODE, DEFAULT).equals(LIGHT))
            themeColor = ThemeUtil.LIGHT_MODE;
        else
            themeColor = ThemeUtil.DEFAULT_MODE;

        ThemeUtil.applyTheme(themeColor);
        ThemeUtil.modSave(context, themeColor);

        darkmodePreference.setSummary(localPrefs.getString(DARKMODE, DEFAULT));
        prefManager.setDarkmode(localPrefs.getString(DARKMODE, DEFAULT));
    }

    private void setLockmode() {
        if (localPrefs.getBoolean(LOCK, false)) {
            lockPreference.setSummary(context.getString(R.string.setting_true));
            prefManager.setLockOn(true);
            moveToLockActivity(LOCKMODE_ON);
        }
        else{
            lockPreference.setSummary(context.getString(R.string.setting_false));
            prefManager.setLockOn(false);
            moveToLockActivity(LOCKMODE_OFF);
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
                    lockPref.setSummary("사용");
                    lockPref.setChecked(true);
                    sharedPref.setLockOn(true);
                    break;
                case RESULT_CANCELED:
                    lockPref.setSummary("사용 안 함");
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
                            lockPref.setSummary("사용 안 함");
                            lockPref.setChecked(false);
                            sharedPref.setLockOn(false);
                            break;
                        case RESULT_CANCELED:
                            lockPref.setSummary("사용");
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
      
    public void moveToLockActivity(int mode) {
        if (mode == LOCKMODE_ON) {
            Intent intent = new Intent(context, PasswordSettingActivity.class);
            lockOnLauncher.launch(intent);
        }
        else {
            Intent intent = new Intent(context, LockActivity.class);
            intent.putExtra("mode", LOCKMODE_OFF);
            lockOffLauncher.launch(intent);
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
        Toast.makeText(context, context.getString(R.string.setting_message), Toast.LENGTH_LONG).show();
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
