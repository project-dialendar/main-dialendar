package com.example.main_dialendar.view.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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

import com.example.main_dialendar.util.theme.ThemeUtil;
import com.example.main_dialendar.model.Day;
import com.example.main_dialendar.R;
import com.example.main_dialendar.view.adapter.CalendarAdapter;
import com.example.main_dialendar.view.adapter.WeekAdapter;
import com.example.main_dialendar.view.dialog.YearPickerDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.BuildConfig;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
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

    // 사이드바 레이아웃
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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

    // 요일 리스트
    private ArrayList<Day> dayList;
    private ArrayList<String> day_of_weekList;

    // 그리드뷰
    private RecyclerView rv_month;
    private GridView gv_day_of_week;

    // 캘린더 변수
    private Calendar mCal;

    public static Context context;

    // 구글 드라이브 상수 & 변수
//    private static final int DRIVE_SIGN_IN = 9002;
//    GoogleSignInClient client;
//    DriveServiceHelper driveServiceHelper;

    // 다크 모드 설정
    String themeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        tv_month = findViewById(R.id.tv_month);

        rv_month = findViewById(R.id.rv_month);
        rv_month.setLayoutManager(new GridLayoutManager(this, 7));
        gv_day_of_week = findViewById(R.id.gv_day_of_week);

        ll_year = findViewById(R.id.btn_year);
        tv_year = findViewById(R.id.tv_year);
        ll_year.setOnClickListener(this);

        btn_write = findViewById(R.id.btn_write);
        btn_write.setOnClickListener(this);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

//        // 구글 로그인 옵션 설정
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.google_default_web_client_id))
//                .requestEmail()
//                .build();

//        client = GoogleSignIn.getClient(this, gso);
//        mAuth = FirebaseAuth.getInstance();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();

                switch(item.getItemId()) {
                    case (R.id.setting) :
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        break;
                    case (R.id.backup) :
                        //requestSignIn();
                        TedPermission.with(MainActivity.this)
                                .setPermissionListener(permissionListener)
                                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                                .check();
                        break;
//                    case (R.id.restore) :
//                        try {
//                            File sd = Environment.getExternalStorageDirectory();
//                            File data = Environment.getDataDirectory();
//
//                            if (sd.canWrite()) {
//                                File currentDB = new File(sd, "/Download/dialendar.db");
//                                File restoreDB = new File(data, "/data/com.example.main_dialendar/databases/dialendar.db");
//
//                                FileChannel src = new FileInputStream(currentDB).getChannel();
//                                FileChannel dst = new FileOutputStream(restoreDB).getChannel();
//                                dst.transferFrom(src, 0, src.size());
//
//                                src.close();
//                                dst.close();
//                                Toast.makeText(MainActivity.this,"복원을 성공했습니다.", Toast.LENGTH_LONG).show();
//                            }
//                        } catch (Exception e) {
//                            Toast.makeText(MainActivity.this,"복원을 실패했습니다.", Toast.LENGTH_LONG).show();
//                        }
//                        break;
                    case (R.id.mail) :
                        sendEmailToAdmin("[일력 문의사항]", new String[]{"apps@gmail.com"});
                        break;
                }
                return true;
            }
        });

        // 상단 툴바 설정
        setToolbar();

        mCal = Calendar.getInstance();

        // bundle에 저장되어 있는 데이터 가져오기
        if(savedInstanceState != null) {
            mCal.set(savedInstanceState.getInt("year"), savedInstanceState.getInt("month"), 1);
        }
        else
            mCal.set(Calendar.DAY_OF_MONTH, 1);
        getCalendar(mCal);

        setThemeMode();
    }

    private void setThemeMode() {
        themeColor = ThemeUtil.modLoad(getApplicationContext());
        ThemeUtil.applyTheme(themeColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        getCalendar(mCal);
    }

    // 구글 드라이브 API - 드라이브 접속 전에 구글 로그인 요청
//    private void requestSignIn() {
//        GoogleSignInOptions signInOptions =
//                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
//                .build();
//        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
//
//        startActivityForResult(client.getSignInIntent(), DRIVE_SIGN_IN);
//    }

    private void setToolbar() {
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
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
        calendarAdapter.setClickListener(this);
        rv_month.setAdapter(calendarAdapter);
    }

    // onClick Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_year :
                YearPickerDialog dialog = new YearPickerDialog();
                //dialog.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setListener(dateSetListener);
                dialog.show(getSupportFragmentManager(), "YearPickerTest");
                break;

            case R.id.btn_write :
                startActivity(new Intent(MainActivity.this, DiaryActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(View view, String day, boolean isInMonth) {
        Calendar itemCal = mCal;
        itemCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        String date = new SimpleDateFormat("yyyy.MM.dd.").format(itemCal.getTime());

        Log.e("Date", date+"");
        if (isInMonth) {
            Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
            intent.putExtra("today", false);
            intent.putExtra("date", date);
            startActivity(intent);
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

    // 구글 드라이브 API - 구글 로그인 화면 연결 메소드
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == DRIVE_SIGN_IN) {
//            if (resultCode == Activity.RESULT_OK && data != null) {
//                handleSignInResult(data);
//            }
//        }
//    }

    // 구글 드라이브 API - 로그인 처리 메소드
//    private void handleSignInResult(Intent result) {
//        GoogleSignIn.getSignedInAccountFromIntent(result)
//                .addOnSuccessListener(googleAccount -> {
//                    GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE_FILE));
//                    credential.setSelectedAccount(googleAccount.getAccount());
//
//                    Drive googleDriverService = new Drive.Builder(
//                            AndroidHttp.newCompatibleTransport(),
//                            new GsonFactory(),
//                            credential)
//                            .setApplicationName("Drive API Migration")
//                            .build();
//
//                    driveServiceHelper = new DriveServiceHelper(googleDriverService);
//
//                    Toast.makeText(MainActivity.this, "로그인 성공!", Toast.LENGTH_LONG);
//                    createFile();
//                })
//                .addOnFailureListener(exception -> Toast.makeText(MainActivity.this, exception + "", Toast.LENGTH_LONG));
//    }
//
    // 구글 드라이브 API - 백업 메소드
//    private void createFile() {
//        if (driveServiceHelper != null) {
//            Log.d("###", "Creating a file.");
//
//            driveServiceHelper.createFile()
//                    .addOnSuccessListener(new OnSuccessListener<String>() {
//                        @Override
//                        public void onSuccess(String s) {
//                            Toast.makeText(MainActivity.this, "백업 성공!", Toast.LENGTH_LONG);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MainActivity.this, "백업 실패!", Toast.LENGTH_LONG);
//                        }
//                    });
//        }
//    }

    // 화면 전환 이후에도 달력 상태를 유지하도록 함
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("year", mCal.get(Calendar.YEAR));
        outState.putInt("month", mCal.get(Calendar.MONTH));
    }

    // 데이터베이스 백업에 필요한 권한 요청
    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    File currentDB = new File(data, "/data/com.example.main_dialendar/databases/dialendar.db");
                    File backupDB = new File(sd, "/Download/dialendar.db");

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());

                    src.close();
                    dst.close();
                    Toast.makeText(MainActivity.this, "백업 성공", Toast.LENGTH_LONG);
                    Log.i("###", "backup success");
                }
                Toast.makeText(MainActivity.this, "백업 실패 in try", Toast.LENGTH_LONG);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "백업 실패", Toast.LENGTH_LONG);
                Log.e("###", "backup failed");
            }
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "접근이 거부되었습니다. 권한을 허용해주세요.", Toast.LENGTH_LONG);
        }
    };
}
