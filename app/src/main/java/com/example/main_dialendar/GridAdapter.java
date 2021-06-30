package com.example.main_dialendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 그리드뷰 어댑터 - 요일 리스트
 */
public class GridAdapter extends BaseAdapter {

    private ArrayList<String> list;
    private Context context;
    private final LayoutInflater inflater;
    private Calendar mCal;

    /**
     * 생성자
     *
     * @param context
     * @param list
     */
    public GridAdapter(Context context, ArrayList<String> list) {
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
        ViewHolder holder = null;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_calendar_day_of_week, parent, false);
            holder = new ViewHolder();
            holder.tvItemGridView = (TextView)convertView.findViewById(R.id.tv_day_of_week);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tvItemGridView.setText("" + getItem(position));

        return convertView;
    }

    private class ViewHolder {
        TextView tvItemGridView;
    }
}

