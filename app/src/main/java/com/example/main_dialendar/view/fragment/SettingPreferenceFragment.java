package com.example.main_dialendar.view.fragment;

import android.content.Intent;
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
import com.example.main_dialendar.util.setting.SharedPrefManager;
import com.example.main_dialendar.util.theme.ThemeUtil;
import com.example.main_dialendar.view.activity.LockActivity;
import com.example.main_dialendar.view.activity.SettingActivity;

/**
 * 실제 설정 목록을 보여주는 프레그먼트 (PreferenceFragment 사용)
 */
public class SettingPreferenceFragment extends PreferenceFragment {

    // 로컬 저장
    public static SharedPreferences localPrefs;
    SharedPrefManager prefManager;

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

        // 변수 세팅
        memoScreen = (PreferenceScreen) findPreference("memo_screen");
        memoPreference = (ListPreference)findPreference("memo_list");
        fontPreference = (ListPreference)findPreference("font_list");
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

    // 디폴트 값 설정
    private void setDefaultInPrefs() {
        if(localPrefs.getBoolean("memo", false))
            memoScreen.setSummary("사용");

        if(!localPrefs.getString("memo_list", "").equals(""))
            memoPreference.setSummary(localPrefs.getString("memo_list", "메모지"));

        if(!localPrefs.getString("font_list", "").equals(""))
            fontPreference.setSummary(localPrefs.getString("memo_list", "Maruburi"));

        if(!localPrefs.getBoolean("message", false))
            messagePreference.setSummary("사용");

        if(localPrefs.getBoolean("lock", false))
            lockPreference.setSummary("사용");

        if(!localPrefs.getString("darkmode", "").equals(""))
            darkmodePreference.setSummary(localPrefs.getString("darkmode", "Default"));
    }

    // 설정 리스트에서 사용되는 리스너
    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // 곧 삭제할 코드
            if (key.equals("memo")) {
                if (localPrefs.getBoolean("memo", false))
                    memoScreen.setSummary("사용");
                else
                    memoScreen.setSummary("사용 안 함");
            }

            if (key.equals("memo_list")) {
                //?
            }

            if (key.equals("font_list"))
                setFont();

            if (key.equals("message"))
                setMessage();

            if (key.equals("darkmode"))
                setDarkmode();

            if (key.equals("lock"))
                setLockmode();

            // 2 deqth PreferenceScreen 내부에서 발생한 설정 내용을 적용시키기 위함
            ((BaseAdapter) getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
        }
    };

    // 폰트 선택 리스너
    private void setFont() {
        if (localPrefs.getString("font_list", "").equals("Maruburi")) {
            //
        }
        else if (localPrefs.getString("font_list", "").equals("Default")){
            //
        }
        else {
            // add
        }

        fontPreference.setSummary(localPrefs.getString("font_list", "Maruburi"));
        prefManager.setFont(localPrefs.getString("font_list", "Maruburi"));
    }

    // 메시지 허용 리스너
    private void setMessage() {
        if (localPrefs.getBoolean("message", false))
            messagePreference.setSummary("사용");
        else
            messagePreference.setSummary("사용 안 함");
    }

    // 다크모드 설정 리스너
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

    // 잠금모드 설정 리스너
    private void setLockmode() {
        if (localPrefs.getBoolean("lock", false)) {
            lockPreference.setSummary("사용");
            prefManager.setLockOn(true);

            Intent intent = new Intent(SettingActivity.context, LockActivity.class);
            intent.putExtra("lock", 10000);
            startActivity(intent);
        }
        else{
            lockPreference.setSummary("사용 안 함");
            prefManager.setLockOn(false);
        }
    }
}
