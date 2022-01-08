package com.example.main_dialendar.view.fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.main_dialendar.R;
import com.example.main_dialendar.util.message.MessageReceiver;
import com.example.main_dialendar.util.setting.SharedPrefManager;
import com.example.main_dialendar.util.theme.ThemeUtil;
import com.example.main_dialendar.view.activity.LockActivity;
import com.example.main_dialendar.view.activity.SettingActivity;
import com.example.main_dialendar.view.dialog.TimePickerDialog;
import com.example.main_dialendar.view.dialog.YearPickerDialog;

import java.util.Calendar;

/**
 * 설정 목록을 보여주는 프레그먼트
 */
public class SettingPreferenceFragment extends PreferenceFragment {

    public static SharedPreferences localPrefs;
    SharedPrefManager prefManager;

    SwitchPreference messagePreference;
    SwitchPreference lockPreference;
    ListPreference darkmodePreference;

    private static final int LOCKMODE_ON = 10000;
    private static final int LOCKMODE_OFF = 99999;

    String themeColor;
    int messageHour = 20, messageMinute = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);  // xml 파일과 연동

        messagePreference = (SwitchPreference)findPreference("message");
        lockPreference = (SwitchPreference)findPreference("lock");
        darkmodePreference = (ListPreference)findPreference("darkmode");

        // sharedPreferences 연결
        prefManager = SharedPrefManager.getInstance(getActivity());
        localPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // 디폴트 값 설정
        setDefaultInPrefs();

        // 리스너 연결
        localPrefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    private void setDefaultInPrefs() {
        if(!localPrefs.getBoolean("message", false))
            messagePreference.setSummary("사용");

        if(localPrefs.getBoolean("lock", false))
            lockPreference.setSummary("사용");

        if(!localPrefs.getString("darkmode", "").equals(""))
            darkmodePreference.setSummary(localPrefs.getString("darkmode", "Default"));
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
        if (localPrefs.getBoolean("message", false)) {
            showMessageDialog();
        }
        else {
            messagePreference.setSummary("사용 안 함");
            prefManager.setMessageOn(false);
        }
    }

    private void setDarkmode() {
        if (localPrefs.getString("darkmode", "Default").equals("Dark"))
            themeColor = ThemeUtil.DARK_MODE;
        else if (localPrefs.getString("darkmode", "Default").equals("Light"))
            themeColor = ThemeUtil.LIGHT_MODE;
        else
            themeColor = ThemeUtil.DEFAULT_MODE;

        ThemeUtil.applyTheme(themeColor);
        ThemeUtil.modSave(SettingActivity.context, themeColor);

        darkmodePreference.setSummary(localPrefs.getString("darkmode", "Default"));
        prefManager.setDarkmode(localPrefs.getString("darkmode", "Default"));
    }

    private void setLockmode() {
        if (localPrefs.getBoolean("lock", false)) {
            lockPreference.setSummary("사용");
            prefManager.setLockOn(true);
            moveToLockActivity(LOCKMODE_ON);
        }
        else{
            lockPreference.setSummary("사용 안 함");
            prefManager.setLockOn(false);
            moveToLockActivity(LOCKMODE_OFF);
        }
    }

    private void showMessageDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity());
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setListener(timeSetListener);
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.setCancelable(true);
        timePickerDialog.show();

    }

    private void moveToLockActivity(int mode) {
        Intent intent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            intent = new Intent(getContext(), LockActivity.class);
        else
            intent = new Intent(SettingActivity.context, LockActivity.class);

        intent.putExtra("lock", mode);
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
        messagePreference.setSummary("사용");
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
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            Intent intent = new Intent(getActivity(), MessageReceiver.class);
            PendingIntent messageIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, messageIntent);

            Toast.makeText(getActivity(), "알림이 설정되었습니다.", Toast.LENGTH_LONG).show();
        }
    }
}
