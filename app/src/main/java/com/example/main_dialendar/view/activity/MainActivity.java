package com.example.main_dialendar.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.main_dialendar.model.Day;
import com.example.main_dialendar.R;
import com.example.main_dialendar.view.adapter.CalendarAdapter;
import com.example.main_dialendar.view.adapter.WeekAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    /**
     * 월/요일 텍스트뷰
     */
    private TextView tv_month;
    private TextView tv_date;

    /**
     * 년도, 글쓰기, 사이드바 버튼
     */
    private Button btn_year;
    private ImageButton btn_write;
    private ImageButton btn_sidebar;

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

        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_date = (TextView) findViewById(R.id.tv_date);

        gv_month = (GridView) findViewById(R.id.gv_month);
        gv_day_of_week = (GridView) findViewById(R.id.gv_day_of_week);

        btn_year = (Button) findViewById(R.id.btn_year);
        btn_write = (ImageButton) findViewById(R.id.btn_write);
        btn_sidebar = (ImageButton) findViewById(R.id.btn_sidebar);

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

}
