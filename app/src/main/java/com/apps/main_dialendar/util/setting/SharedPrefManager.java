package com.apps.main_dialendar.util.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

// 설정 정보 관리 및 저장 매니저
public class SharedPrefManager {

    private static SharedPrefManager manager;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final String SHARED_PREFS_FILE_NAME = "Dialendar";
    private static final String LOCK_KEY = "LockOnOff";
    private static final String PASSWORD_KEY = "Password";
    private static final String DARKMODE_KEY = "Darkmode";
    private static final String MESSAGE_KEY = "MessageOnOff";
    private static final String MESSAGE_HOUR_KEY = "MessageHour";
    private static final String MESSAGE_MINUTE_KEY = "MessageMinute";
    public static final String LIGHT_MODE = "Light";
    public static final String DARK_MODE = "Dark";
    public static final String DEFAULT_MODE = "Default";

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(
                SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();
    }

    // 싱글톤 instance 반환
    public static SharedPrefManager getInstance(Context context) {
        if (manager == null) {
            manager = new SharedPrefManager(context);
        }
        return manager;
    }

    public void setLockOn(boolean value) {
        editor.putBoolean(LOCK_KEY, value);
        editor.commit();
    }

    public boolean getLockOff() {
        return sharedPreferences.getBoolean(LOCK_KEY, false);
    }


    public void setPassword(int pw) {
        editor.putInt(PASSWORD_KEY, pw);
        editor.commit();
    }

    public int getPassword() { return sharedPreferences.getInt(PASSWORD_KEY, -1); }


    public void setDarkmode(String mode) {
        // {Default, Light, Dark}
        editor.putString(DARKMODE_KEY, mode);
        editor.commit();
    }

    public String getDarkmode() {
        return sharedPreferences.getString(DARKMODE_KEY, "Default");
    }

    public static void applyTheme(String themeColor) {
        switch (themeColor) {
            case LIGHT_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;

            case DARK_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;

            default :
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }

        }
    }

    public void setMessageOn(boolean value, int hour, int minute) {
        editor.putBoolean(MESSAGE_KEY, value);
        if (value) {
            editor.putInt(MESSAGE_HOUR_KEY, hour);
            editor.putInt(MESSAGE_MINUTE_KEY, minute);
        }
        editor.commit();
    }

    public void setMessageOn(boolean value) {
        editor.putBoolean(MESSAGE_KEY, value);
        editor.commit();
    }

    public int getMessageHour() {
        return sharedPreferences.getInt(MESSAGE_HOUR_KEY, 20);
    }

    public int getMessageMinute() {
        return sharedPreferences.getInt(MESSAGE_MINUTE_KEY, 00);
    }

    public boolean getMessageOff() {
        return sharedPreferences.getBoolean(MESSAGE_KEY, false);
    }

    // clear
    public static void destroyPref() {
        editor.clear().apply();
    }
}
