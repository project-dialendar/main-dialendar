package com.example.main_dialendar.util.setting;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static SharedPrefManager mInstance;
    private static SharedPreferences mSharedPrefs;
    private static SharedPreferences.Editor mEdit;

    private static final String SHARED_PREFS_FILE_NAME = "Dialendar";

    private static final String FONT_KEY = "Maruburi";
    private static final String LOCK_KEY = "LockOnOff";
    private static final String PW_KEY = "Password";

    public SharedPrefManager(Context context) {
        mSharedPrefs = context.getSharedPreferences(
                SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE
        );
        mEdit = mSharedPrefs.edit();
    }

    public static SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void setFont(String font) {
        mEdit.putString(FONT_KEY, font);
        mEdit.commit();
    }

    public String getFont() {
        return mSharedPrefs.getString(FONT_KEY, "");
    }

    public void setLockOn(boolean value) {
        mEdit.putBoolean(LOCK_KEY, value);
        mEdit.commit();
    }

    public boolean getLockOff() {
        return mSharedPrefs.getBoolean(LOCK_KEY, false);
    }

    public void setPassword(int pw) {
        mEdit.putInt(PW_KEY, pw);
        mEdit.commit();
    }

    public int getPassword() { return mSharedPrefs.getInt(PW_KEY, -1); }

    // clear
    public static void destroyPref() {
        mEdit.clear().apply();
    }
}
