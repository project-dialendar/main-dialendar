package com.example.main_dialendar.view.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.main_dialendar.BuildConfig;
import com.example.main_dialendar.WritingDialog;
import com.example.main_dialendar.model.Day;
import com.example.main_dialendar.R;
import com.example.main_dialendar.view.adapter.CalendarAdapter;
import com.example.main_dialendar.view.adapter.WeekAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    /**
     * 월/요일 텍스트뷰
     */
    private TextView tv_month;
    private TextView tv_date;

    /**
     * 년도, 글쓰기 버튼, 사이드바 레이아웃
     */
    private Button btn_year;
    private ImageButton btn_write;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    /**
     * 그리드뷰 어댑터
     */
    private CalendarAdapter calendarAdapter;
    private WeekAdapter day_of_weekGridAdapter;

    /**
     * 요일 리스트
     */
    private ArrayList<Day> dayList;
    private ArrayList<String> day_of_weekList;

    /**
     * 그리드뷰
     */
    private GridView gv_month;
    private GridView gv_day_of_week;

    /**
     * 캘린더 변수
     */
    private Calendar mCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_month = findViewById(R.id.tv_month);
        tv_date = findViewById(R.id.tv_date);

        gv_month = findViewById(R.id.gv_month);
        gv_day_of_week = findViewById(R.id.gv_day_of_week);

        btn_year = findViewById(R.id.btn_year);
        btn_write = findViewById(R.id.btn_write);
        drawerLayout = findViewById(R.id.drawerLayout);

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();

                switch(item.getItemId()) {
                    case (R.id.setting) :
                        break;
                    case (R.id.backup) :
                        break;
                    case (R.id.mail) :
                        sendEmailToAdmin("[일력 문의사항]", new String[]{"apps@gmail.com"});
                        break;
                }
                return true;
            }
        });

        // 상단 툴바 설정
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowCustomEnabled(true);    // 커스터마이징을 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // 툴바 메뉴 버튼 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_sidebar); // 메뉴 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
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

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writingDialog(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 캘린더 인스턴스 생성
        mCal = Calendar.getInstance();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        getCalendar(mCal);
    }

    /**
     * 달력 세팅
     * @param calendar
     */
    private void getCalendar(Calendar calendar) {
        int lastMonthStartDay;
        int dayOfMonth;
        int thisMonthLastDay;

        dayList.clear();

        // 이번달 시작 요일
        dayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, -1);
        Log.e("지난달 마지막일", calendar.get(Calendar.DAY_OF_MONTH)+"");

        // 지난달 마지막 일자
        lastMonthStartDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, 1);
        Log.e("이번달 시작일", calendar.get(Calendar.DAY_OF_MONTH)+"");

        lastMonthStartDay -= (dayOfMonth-1)-1;

        // 년월 표시
        tv_month.setText((mCal.get(Calendar.MONTH)+1)+"");
        btn_year.setText(mCal.get(Calendar.YEAR)+"");

        Day day;
        Log.e("DayOfMonth", dayOfMonth+"");

        for(int i=0; i<dayOfMonth-1; i++){
            int date = lastMonthStartDay+i;
            day = new Day();
            day.setDay(Integer.toString(date));
            day.setInMonth(false);

            dayList.add(day);
        }

        for(int i=1; i<=thisMonthLastDay; i++){
            day = new Day();
            day.setDay(Integer.toString(i));
            day.setInMonth(true);

            dayList.add(day);
        }

        for(int i=1; i<35-(thisMonthLastDay+dayOfMonth)+1; i++){
            day = new Day();
            day.setDay(Integer.toString(i));
            day.setInMonth(false);
            dayList.add(day);
        }

        initCalendarAdapter();
    }

    private void initCalendarAdapter() {
        calendarAdapter = new CalendarAdapter(this, dayList);
        gv_month.setAdapter(calendarAdapter);
    }

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



    /**
     * 글쓰기 다이얼로그
     * @param v
     */

    public void writingDialog(View v){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_writing, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Intent.ACTION_SEND로 이메일 보내기
     * @param title
     * @param receiver
     */
    public void sendEmailToAdmin(String title, String[] receiver) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, receiver);
        email.putExtra(Intent.EXTRA_SUBJECT, title);
        email.putExtra(Intent.EXTRA_TEXT, "앱 버전(AppVersion):" + BuildConfig.VERSION_NAME +
                "\n기기명(Device): \n안드로이드 OS(Android OS): \n내용(Content): \n");
        email.setType("message/rfc822");
        startActivity(email);
    }
}
