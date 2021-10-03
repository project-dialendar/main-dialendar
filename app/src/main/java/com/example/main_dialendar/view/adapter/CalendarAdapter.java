package com.example.main_dialendar.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

    int year;
    int month;

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
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_calendar_gridview, null);

            holder = new ViewHolder();
            holder.tv_item = (TextView)convertView.findViewById(R.id.tv_date);
            holder.iv_item = (ImageView)convertView.findViewById(R.id.iv_date);
            holder.iv_item.setClipToOutline(true);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        if(day != null) {
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
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.getDay()));

                String date = mFormat.format(cal.getTime());
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


}