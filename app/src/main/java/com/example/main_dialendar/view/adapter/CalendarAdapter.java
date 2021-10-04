package com.example.main_dialendar.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.main_dialendar.database.Diary;
import com.example.main_dialendar.database.DiaryDao;
import com.example.main_dialendar.database.DiaryDatabase;
import com.example.main_dialendar.model.Day;
import com.example.main_dialendar.R;
import com.example.main_dialendar.view.activity.DiaryActivity;
import com.example.main_dialendar.view.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 그리드뷰 어댑터 - 캘린더
 */
public class CalendarAdapter extends BaseAdapter {
    private ArrayList<Day> list;
    private Context context;
    private final LayoutInflater inflater;

    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd.");
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");

    int year;
    int month;

    /* 데이터베이스 */
    private DiaryDao mDiaryDao;

    /**
     * 생성자
     *
     * @param context
     * @param list
     */
    public CalendarAdapter(Context context, int year, int month, ArrayList<Day> list) {
        this.context = context;
        this.year = year;
        this.month = month;
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Day day = list.get(position);

        ViewHolder holder = null;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.getDay()));
        // 날짜 스트링 생성
        String date = mFormat.format(cal.getTime());
        String dbDate = dbFormat.format(cal.getTime());
        /* 데이터베이스 생성 */
        DiaryDatabase database = DiaryDatabase.getInstance(context);
        mDiaryDao = database.diaryDao();                  // 인터페이스 객체 할당

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_calendar_gridview, null);

            holder = new ViewHolder();
            holder.tv_item = (TextView) convertView.findViewById(R.id.tv_date);
            holder.iv_item = (ImageView) convertView.findViewById(R.id.iv_date);

            // 해당 날짜에 레코드가 존재하는지 확인
            Diary diaryRecord = mDiaryDao.findByDate(dbDate);

            try {// 이미지 삽입
                holder.iv_item.setImageBitmap(getImageInBitmap(diaryRecord.getImage()));
            } catch (NullPointerException e) {

            }
//            if (diaryRecord.getImage() != null) {
//                holder.iv_item.setImageBitmap(getImageInBitmap(diaryRecord.getImage()));
//            }

            holder.iv_item.setClipToOutline(true);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (day != null) {
            holder.tv_item.setText(day.getDay());
            if(day.isInMonth()){
                if(position % 7 == 0){
                    holder.tv_item.setTextColor(Color.parseColor("#C40000"));
                    holder.iv_item.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                else{
                    holder.tv_item.setTextColor(Color.BLACK);
                    holder.iv_item.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }
            else{
                holder.tv_item.setTextColor(Color.GRAY);
                holder.iv_item.setBackgroundColor(Color.parseColor("#e8e8e8"));
            }
        }
        holder.iv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Calendar cal = Calendar.getInstance();
//                cal.set(Calendar.YEAR, year);
//                cal.set(Calendar.MONTH, month);
//                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.getDay()));
//
//                String date = mFormat.format(cal.getTime());
                Intent intent = new Intent(context, DiaryActivity.class);
                intent.putExtra("today", false);
                intent.putExtra("date", date);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView tv_item;
        ImageView iv_item;
    }

    /**
     * convert from byte array to bitmap
     * @param image byte array
     * @return bitmap image
     */
    public static Bitmap getImageInBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}