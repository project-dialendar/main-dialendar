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

import java.util.Calendar;

/**
 * 설정 목록을 보여주는 프레그먼트
 */
public class SettingPreferenceFragment extends PreferenceFragment {

    public static SharedPreferences localPrefs;

    public static final String MESSAGE = "message";
    public static final String LOCK = "lock";
    public static final String DARKMODE = "darkmode";
    private static final String LIGHT = "Light";
    private static final String DARK = "Dark";
    private static final String DEFAULT = "Default";

    private static final int LOCKMODE_ON = 10000;
    private static final int LOCKMODE_OFF = 99999;

    SharedPrefManager prefManager;

    SwitchPreference messagePreference;
    SwitchPreference lockPreference;
    ListPreference darkmodePreference;

    String themeColor;
    int messageHour = 20, messageMinute = 0;

    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);  // xml 파일과 연동

        context = getActivity();

        messagePreference = (SwitchPreference)findPreference(MESSAGE);
        lockPreference = (SwitchPreference)findPreference(LOCK);
        darkmodePreference = (ListPreference)findPreference(DARKMODE);

        // sharedPreferences 연결
        prefManager = SharedPrefManager.getInstance(context);
        localPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        // 디폴트 값 설정
        setDefaultInPrefs();

        // 리스너 연결
        localPrefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    private void setDefaultInPrefs() {
        if(!localPrefs.getBoolean(MESSAGE, false))
            messagePreference.setSummary(context.getString(R.string.setting_true));

        if(localPrefs.getBoolean(LOCK, false))
            lockPreference.setSummary(context.getString(R.string.setting_true));

        if(!localPrefs.getString(DARKMODE, "").equals(""))
            darkmodePreference.setSummary(localPrefs.getString(DARKMODE, DEFAULT));
    }

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals(MESSAGE)) setMessage();
            if (key.equals(DARKMODE)) setDarkmode();
            if (key.equals(LOCK)) setLockmode();

            // 2 deqth PreferenceScreen 내부에서 발생한 설정 내용을 적용시키기 위함
            ((BaseAdapter) getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
        }
    };

    private void setMessage() {
        if (localPrefs.getBoolean(MESSAGE, false)) {
            messagePreference.setSummary(context.getString(R.string.setting_true));
            showMessageDialog();
        }
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
        ThemeUtil.modSave(SettingActivity.context, themeColor);

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

    private void showMessageDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setListener(timeSetListener);
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.setCancelable(true);
        timePickerDialog.show();

    }

    private void moveToLockActivity(int mode) {
        Intent intent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            intent = new Intent(context, LockActivity.class);
        else
            intent = new Intent(SettingActivity.context, LockActivity.class);

        intent.putExtra(LOCK, mode);
        startActivity(intent);
    }

    android.app.TimePickerDialog.OnTimeSetListener timeSetListener = new android.app.TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            // save message's hour and minute
            messageHour = hour;
            messageMinute = minute;

            setMessagePrefOn();
        }
    };

    private void setMessagePrefOn() {
        prefManager.setMessageOn(true, messageHour, messageMinute);

        startMessaging();
    }

    private void startMessaging() {
        Calendar cal = createMessageTime();
        startAlarmManager(cal);
    }

    private Calendar createMessageTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, messageHour);
        cal.set(Calendar.MINUTE, messageMinute);

        if (cal.before(Calendar.getInstance()))
            cal.add(Calendar.DATE, 1);
        return cal;
    }

    private void startAlarmManager(Calendar cal) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            Intent intent = new Intent(context, MessageReceiver.class);
            PendingIntent messageIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, messageIntent);

            Toast.makeText(context, context.getString(R.string.setting_message), Toast.LENGTH_LONG).show();
        }
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
