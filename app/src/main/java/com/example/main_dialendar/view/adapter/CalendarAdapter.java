package com.example.main_dialendar.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.main_dialendar.model.Day;
import com.example.main_dialendar.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 그리드뷰 어댑터 - 캘린더
 */
public class CalendarAdapter extends BaseAdapter {
    private ArrayList<Day> list;
    private Context context;
    private final LayoutInflater inflater;
    private Calendar mCal;

    /**
     * 생성자
     *
     * @param context
     * @param list
     */
    public CalendarAdapter(Context context, ArrayList<Day> list) {
        this.context = context;
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
                if(position % 7 == 0)
                    holder.tv_item.setTextColor(Color.RED);
                else
                    holder.tv_item.setTextColor(Color.GRAY);
            }
            else{
                holder.tv_item.setTextColor(Color.LTGRAY);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tv_item;
        ImageView iv_item;
    }
}