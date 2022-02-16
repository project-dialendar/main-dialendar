package com.example.main_dialendar.view.activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_dialendar.util.setting.SharedPrefManager;
import com.example.main_dialendar.model.Day;
import com.example.main_dialendar.R;
import com.example.main_dialendar.view.adapter.CalendarAdapter;
import com.example.main_dialendar.view.adapter.WeekAdapter;
import com.example.main_dialendar.view.dialog.BackupDialog;
import com.example.main_dialendar.view.dialog.YearPickerDialog;
import com.google.android.material.navigation.NavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CalendarAdapter.ItemClickListener {

    // 월/요일 텍스트뷰
    private TextView tv_month;

    // 년도, 글쓰기 버튼
    private LinearLayout ll_year;
    private TextView tv_year;
    private ImageButton btn_write;

    // 년도 버튼 클릭 이벤트 리스너
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            setCalendarView(calendar);
        }
    };

    // 사이드바 레이아웃
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // 캘린더뷰 어댑터
    private CalendarAdapter calendarAdapter;
    private WeekAdapter day_of_weekGridAdapter;

    // 요일 리스트
    private ArrayList<Day> dayList;
    private ArrayList<String> day_of_weekList;

    // 그리드뷰
    private RecyclerView rv_month;
    private GridView gv_day_of_week;

    // 캘린더 변수
    private Calendar calendar;

    // 다크 모드 설정
    private String themeColor;

    public static Context context;
    private SharedPrefManager sharedPref;
    private boolean lock = true;

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DATE = "date";
    private static final String IS_TODAY = "today";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 변수 세팅
        context = getApplicationContext();

        tv_month = findViewById(R.id.tv_month);

        rv_month = findViewById(R.id.rv_month);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 7);
        rv_month.setLayoutManager(gridLayoutManager);
        rv_month.setItemViewCacheSize(42);
        gv_day_of_week = findViewById(R.id.gv_day_of_week);

        ll_year = findViewById(R.id.btn_year);
        tv_year = findViewById(R.id.tv_year);
        ll_year.setOnClickListener(this);

        btn_write = findViewById(R.id.btn_write);
        btn_write.setOnClickListener(this);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        sharedPref = SharedPrefManager.getInstance(this);

        // 드로어바 메뉴 세팅
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case (R.id.setting):
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        break;
                    case (R.id.backup):
                        checkBackupPermission();
                        break;
                    case (R.id.mail):
                        sendEmailToAdmin("[일력 문의사항]", new String[]{"apps@gmail.com"});
                        break;
                }
                return true;
            }
        });

        // 상단 툴바 설정
        setToolbar();

        // 캘린더뷰 생성
        setCalendarView(getCalendar(savedInstanceState));

        // darkmode setting
        setThemeMode();
    }

    // 캘린더 인스턴스 생성 또는 불러오기
    private Calendar getCalendar(Bundle savedInstanceState) {

        // 캘린더 인스턴스 생성
        calendar = Calendar.getInstance();

        // bundle에 저장되어 있는 데이터 가져오기
        if (savedInstanceState != null) {
            calendar.set(savedInstanceState.getInt(YEAR), savedInstanceState.getInt(MONTH), 1);
        } else
            calendar.set(Calendar.DAY_OF_MONTH, 1);

        return calendar;
    }

    // 다크모드 여부에 따라 테마 설정
    private void setThemeMode() {
        themeColor = sharedPref.getDarkmode();
        sharedPref.applyTheme(themeColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        setCalendarView(calendar);
    }

    // 툴바 설정
    private void setToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowCustomEnabled(true);    // 커스터마이징을 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // 툴바 메뉴 버튼 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_sidebar); // 메뉴 버튼 모양 설정
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.background)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //gridview_day_of_week에 요일 리스트 표시
        day_of_weekList = new ArrayList<String>();
        day_of_weekList.add("SUN");
        day_of_weekList.add("MON");
        day_of_weekList.add("TUE");
        day_of_weekList.add("WED");
        day_of_weekList.add("THU");
        day_of_weekList.add("FRI");
        day_of_weekList.add("SAT");

        day_of_weekGridAdapter = new WeekAdapter(this, day_of_weekList);
        gv_day_of_week.setAdapter(day_of_weekGridAdapter);

        dayList = new ArrayList<Day>();
    }

    /**
     * 달력 세팅
     *
     * @param calendar
     */
    private void setCalendarView(Calendar calendar) {
        int lastMonthStartDay;
        int dayOfMonth;
        int thisMonthLastDay;

        dayList.clear();

        // 이번달 시작 요일
        dayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, -1);

        // 지난달 마지막 일자
        lastMonthStartDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, 1);

        lastMonthStartDay -= (dayOfMonth - 1) - 1;

        // 년월 표시
        tv_month.setText((this.calendar.get(Calendar.MONTH) + 1) + "");
        tv_year.setText(this.calendar.get(Calendar.YEAR) + "");

        Day day;
        for (int i = 0; i < dayOfMonth - 1; i++) {
            int date = lastMonthStartDay + i;
            day = new Day();
            day.setDay(Integer.toString(date));
            day.setInMonth(false);

            dayList.add(day);
        }

        for (int i = 1; i <= thisMonthLastDay; i++) {
            day = new Day();
            day.setDay(Integer.toString(i));
            day.setInMonth(true);

            dayList.add(day);
        }

        for (int i = 1; i < 35 - (thisMonthLastDay + dayOfMonth) + 1; i++) {
            day = new Day();
            day.setDay(Integer.toString(i));
            day.setInMonth(false);
            dayList.add(day);
        }

        initCalendarAdapter();
    }

    // 캘린더뷰 어댑터 초기화
    private void initCalendarAdapter() {
        calendarAdapter = new CalendarAdapter(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), dayList);
        calendarAdapter.setClickListener(this);
        rv_month.setAdapter(calendarAdapter);
    }

    // onClick Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_year:
                showYearPickerDialog();
                break;

            case R.id.btn_write:
                startActivity(new Intent(MainActivity.this, DiaryActivity.class));
                break;
        }
    }

    // 캘린더뷰 아이템 클릭 리스너
    @Override
    public void onItemClick(View view, String day, boolean isInMonth) {
        Calendar itemCal = calendar;
        itemCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        String date = new SimpleDateFormat("yyyy.MM.dd.").format(itemCal.getTime());

        if (isInMonth) {
            Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
            intent.putExtra(IS_TODAY, false);
            intent.putExtra(DATE, date);
            startActivity(intent);
        }
    }

    void showYearPickerDialog() {
        YearPickerDialog dialog = new YearPickerDialog(MainActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setListener(dateSetListener);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    // 드로어바 클릭 리스너
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkBackupPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(intent, 300);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 300);
                }
            } else {
                moveToBackupDialog();
            }
        }
        else {
            TedPermission.create()
                    .setPermissionListener(permissionListener)
                    .setRationaleMessage("[설정] - [권한] 에서 모든 파일에 대한 액세스 권한을 허용해주세요.")
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                    .check();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager())
                    moveToBackupDialog();
                else
                    finish();
            }
        }
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            moveToBackupDialog();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(context, "접근이 거부되었습니다. 권한을 허용해주세요.", Toast.LENGTH_LONG);
        }
    };

    private void moveToBackupDialog() {
        BackupDialog dialog = new BackupDialog(MainActivity.this);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    public void sendEmailToAdmin(String title, String[] receiver) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, receiver);
        email.putExtra(Intent.EXTRA_SUBJECT, title);
        email.putExtra(Intent.EXTRA_TEXT, "앱 버전(AppVersion):" +
                "\n기기명(Device): \n안드로이드 OS(Android OS): \n내용(Content): \n");
        email.setType("message/rfc822");
        startActivity(email);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPref.getLockOff() && lock) {
            lock = false;
            startActivity(new Intent(this, LockActivity.class));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(YEAR, calendar.get(Calendar.YEAR));
        outState.putInt(MONTH, calendar.get(Calendar.MONTH));
    }
}
