package com.example.main_dialendar.util.setting;

import android.content.Context;
import android.content.SharedPreferences;

// 설정 정보 관리 및 저장 매니저
public class SharedPrefManager {

    private static SharedPrefManager manager;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final String SHARED_PREFS_FILE_NAME = "Dialendar";

    // 폰트
    private static final String FONT_KEY = "Maruburi";

    // 잠금화면
    private static final String LOCK_KEY = "LockOnOff";

    // 패스워드
    private static final String PW_KEY = "Password";

    // 다크모드
    private static final String MODE_KEY = "Darkmode";

    // manager 초기 세팅
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

    public void setFont(String font) {
        editor.putString(FONT_KEY, font);
        editor.commit();
    }

    public String getFont() {
        return sharedPreferences.getString(FONT_KEY, "");
    }


    public void setLockOn(boolean value) {
        editor.putBoolean(LOCK_KEY, value);
        editor.commit();
    }

    public boolean getLockOff() {
        return sharedPreferences.getBoolean(LOCK_KEY, false);
    }


    public void setPassword(int pw) {
        editor.putInt(PW_KEY, pw);
        editor.commit();
    }

    public int getPassword() { return sharedPreferences.getInt(PW_KEY, -1); }


    public void setDarkmode(String mode) {
        // {Default, Light, Dark}
        editor.putString(MODE_KEY, mode);
        editor.commit();
    }

    public String getDarkmode() {
        return sharedPreferences.getString(MODE_KEY, "Default");
    }


    // clear
    public static void destroyPref() {
        editor.clear().apply();
    }
}
