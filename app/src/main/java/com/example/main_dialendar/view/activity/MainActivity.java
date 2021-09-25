package com.example.main_dialendar.view.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.main_dialendar.BuildConfig;
import com.example.main_dialendar.model.Day;
import com.example.main_dialendar.R;
import com.example.main_dialendar.view.adapter.CalendarAdapter;
import com.example.main_dialendar.view.adapter.WeekAdapter;
import com.example.main_dialendar.view.dialog.YearPickerDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 월/요일 텍스트뷰
    private TextView tv_month;
    private TextView tv_date;

    // 년도, 글쓰기 버튼
    private LinearLayout ll_year;
    private TextView tv_year;
    private ImageButton btn_write;

    // 사이드바 레이아웃
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView iv_profile;
    private TextView tv_profile;

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mCal.set(Calendar.YEAR, year);
            mCal.set(Calendar.MONTH, month);
            getCalendar(mCal);
        }
    };

    // 그리드뷰 어댑터
    private CalendarAdapter calendarAdapter;
    private WeekAdapter day_of_weekGridAdapter;
    private int cellSize = 0;

    // 요일 리스트
    private ArrayList<Day> dayList;
    private ArrayList<String> day_of_weekList;

    // 그리드뷰
    private GridView gv_month;
    private GridView gv_day_of_week;

    // 캘린더 변수
    private Calendar mCal;

    // 구글 로그인 상수 & 변수
    private static final int SIGN_IN = 9001;
    GoogleSignInClient client;
    private FirebaseAuth mAuth;

    // 기기 별 기준 사이즈와 해상도
    int standardSize_X, standardSize_Y;
    float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 접속한 기기의 해상도 확인
        getStandardSize();

        tv_month = findViewById(R.id.tv_month);
        tv_month.setTextSize((float) (standardSize_X/7));

        tv_date = findViewById(R.id.tv_date);

        gv_month = findViewById(R.id.gv_month);
        gv_day_of_week = findViewById(R.id.gv_day_of_week);
        LinearLayout ll_calendar = findViewById(R.id.ll_calendar);
        cellSize = standardSize_X / 49 * 5;

        ll_year = findViewById(R.id.btn_year);
        tv_year = findViewById(R.id.tv_year);
        ll_year.setOnClickListener(this);

        btn_write = findViewById(R.id.btn_write);
        btn_write.setOnClickListener(this);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        View nav_header_view = navigationView.getHeaderView(0);
        iv_profile = nav_header_view.findViewById(R.id.iv_profile);
        tv_profile = nav_header_view.findViewById(R.id.tv_profile);

        // 구글 로그인 옵션 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_default_web_client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();

                switch(item.getItemId()) {
                    case (R.id.setting) :
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        break;
                    case (R.id.backup) :
                        break;
                    case (R.id.mail) :
                        sendEmailToAdmin("[일력 문의사항]", new String[]{"apps@gmail.com"});
                        break;
                    case (R.id.login) :
                        signIn();
                        break;
                }
                return true;
            }
        });

        // 상단 툴바 설정
        setToolbar();
    }

    private void setToolbar() {
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
    }
/*
    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
*/
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
        tv_year.setText(mCal.get(Calendar.YEAR)+"");

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
        calendarAdapter = new CalendarAdapter(this, mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), dayList);
        gv_month.setAdapter(calendarAdapter);
    }

    // onClick Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_year :
                YearPickerDialog dialog = new YearPickerDialog();
                dialog.setListener(dateSetListener);
                dialog.show(getSupportFragmentManager(), "YearPickerTest");
                break;

            case R.id.btn_write :
                startActivity(new Intent(MainActivity.this, DiaryActivity.class));
                break;
        }
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

    // 구글 로그인 메소드
    private void signIn() {
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {   // 구글 로그인
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());   // 구글 계정 권한 부여
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_LONG);
            }
        }
    }

    // 구글 계정을 파이어베이스에 등록한 뒤, 토큰을 반환하는 메소드
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(MainActivity.this, "구글 로그인 성공", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "구글 로그인 실패", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // 로그인 성공 후, 사이드바 헤더 변경 메소드
    private void updateUI(FirebaseUser user) {
        String userName = user.getDisplayName();
        Uri userImage = user.getPhotoUrl();

        tv_profile.setText(userName + " 님, 환영합니다!");
        iv_profile.setImageURI(userImage);
    }

    // 기기 별 기준 해상도를 계산
    public void getStandardSize() {
        Point ScreenSize = getScreenSize(this);
        density  = getResources().getDisplayMetrics().density;

        standardSize_X = (int) (ScreenSize.x / density);
        standardSize_Y = (int) (ScreenSize.y / density);
    }

    // 기기 별 해상도 반환
    public Point getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return  size;
    }

}
