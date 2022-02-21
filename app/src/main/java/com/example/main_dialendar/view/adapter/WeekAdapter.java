package com.example.main_dialendar.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_dialendar.R;
import com.example.main_dialendar.database.Diary;
import com.example.main_dialendar.model.Day;
import com.example.main_dialendar.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 그리드뷰 어댑터 - 요일 리스트
 */
public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {

    private ArrayList<String> list;
    private Context context;
    private final LayoutInflater inflater;

    /**
     * 생성자
     *
     * @param context
     * @param list
     */
    public WeekAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public WeekAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_calendar_day_of_week, parent, false);
        return new WeekAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekAdapter.ViewHolder holder, int position) {
        holder.tv_date_of_week.setText("" + list.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date_of_week;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date_of_week = itemView.findViewById(R.id.tv_day_of_week);
        }
    }
}

