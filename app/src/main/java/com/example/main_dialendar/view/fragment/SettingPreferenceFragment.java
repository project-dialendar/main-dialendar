package com.example.main_dialendar.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;

import com.example.main_dialendar.R;
import com.example.main_dialendar.util.ThemeUtil;
import com.example.main_dialendar.view.activity.MainActivity;
import com.example.main_dialendar.view.activity.SettingActivity;

/**
 * 실제 설정 목록을 보여주는 프레그먼트 (PreferenceFragment 사용)
 */
public class SettingPreferenceFragment extends PreferenceFragment {

    // 로컬 저장
    SharedPreferences prefs;

    // 메모지 설정
    PreferenceScreen memoScreen;
    ListPreference memoPreference;

    // 폰트 설정
    ListPreference fontPreference;

    // 알림 설정
    SwitchPreference messagePreference;

    // 잠금 모드 설정
    SwitchPreference lockPreference;

    // 다크 모드 설정
    ListPreference darkmodePreference;
    String themeColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_preference);  // xml 파일과 연동
        memoScreen = (PreferenceScreen) findPreference("memo_screen");
        memoPreference = (ListPreference)findPreference("memo_list");
        fontPreference = (ListPreference)findPreference("font_list");
        messagePreference = (SwitchPreference)findPreference("message");
        lockPreference = (SwitchPreference)findPreference("lock");
        darkmodePreference = (ListPreference)findPreference("darkmode");

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // 디폴트 값 설정
        if(prefs.getBoolean("memo", false)) {
            memoScreen.setSummary("사용");
        }
        if(!prefs.getString("memo_list", "").equals("")) {
            memoPreference.setSummary(prefs.getString("memo_list", "메모지"));
        }

        if(!prefs.getString("font_list", "").equals("")) {
            fontPreference.setSummary(prefs.getString("memo_list", "굴림"));
        }

        if(!prefs.getBoolean("message", false)) {
            messagePreference.setSummary("사용");
        }

        if(prefs.getBoolean("lock", false)) {
            lockPreference.setSummary("사용");
        }

        if(!prefs.getString("darkmode", "").equals("")) {
            darkmodePreference.setSummary(prefs.getString("darkmode", "Default"));
        }

        // 리스너 연결
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    // 설정 리스트에서 사용되는 리스너 (아직 구현 x)
    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals("memo")) {
                if (prefs.getBoolean("memo", false))
                    memoScreen.setSummary("사용");
                else
                    memoScreen.setSummary("사용 안 함");
            }

            if (key.equals("memo_list")) {

            }

            if (key.equals("font_list")) {


            }

            if (key.equals("message")) {
                if (prefs.getBoolean("message", false))
                    messagePreference.setSummary("사용");
                else
                    messagePreference.setSummary("사용 안 함");
            }

            if (key.equals("darkmode")) {
                if (prefs.getString("darkmode", "").equals("Dark")) {
                    darkmodePreference.setSummary("Dark");
                    themeColor = ThemeUtil.DARK_MODE;
                }
                else if (prefs.getString("darkmode", "").equals("Light")){
                    darkmodePreference.setSummary("Light");
                    themeColor = ThemeUtil.LIGHT_MODE;
                }
                else {
                    darkmodePreference.setSummary("Default");
                    themeColor = ThemeUtil.DEFAULT_MODE;
                }

                ThemeUtil.applyTheme(themeColor);
                ThemeUtil.modSave(SettingActivity.context, themeColor);
            }

            if (key.equals("lock")) {
                if (prefs.getBoolean("lock", false))
                    lockPreference.setSummary("사용");
                else
                    lockPreference.setSummary("사용 안 함");
            }

            // 2 deqth PreferenceScreen 내부에서 발생한 설정 내용을 적용시키기 위함
            ((BaseAdapter) getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
        }
    };
}
