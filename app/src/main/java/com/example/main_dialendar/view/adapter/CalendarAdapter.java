package com.example.main_dialendar.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_dialendar.database.Diary;
import com.example.main_dialendar.database.DiaryDao;
import com.example.main_dialendar.database.DiaryDatabase;
import com.example.main_dialendar.model.Day;
import com.example.main_dialendar.R;
import com.example.main_dialendar.view.activity.DiaryActivity;
import com.example.main_dialendar.view.activity.MainActivity;
import com.example.main_dialendar.view.fragment.SettingPreferenceFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 그리드뷰 어댑터 - 캘린더
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    // {0 ~ 30/31} 까지의 날짜 리스트
    private ArrayList<Day> list;

    private Context context;
    private final LayoutInflater inflater;

    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd.");
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");

    Calendar cal = Calendar.getInstance();
    ItemClickListener mClickListener;

    /* 데이터베이스 */
    DiaryDatabase database;
    private DiaryDao mDiaryDao;

    /**
     * 생성자
     *
     * @param context
     * @param list
     */
    public CalendarAdapter(Context context, int year, int month, ArrayList<Day> list) {
        this.context = context;
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 캘린더 세팅
        this.cal.set(Calendar.YEAR, year);
        this.cal.set(Calendar.MONTH, month);

        // DB 세팅
        this.database = DiaryDatabase.getInstance(context);
        mDiaryDao = database.diaryDao();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Day day = list.get(position);

        if (day != null) {
            holder.tv_item.setText(day.getDay());
            if (day.isInMonth() == true) {
                // 날짜 스트링 생성
                String dbDate = getDate(day.getDay(), true);

                // 해당 날짜에 레코드가 존재하는지 확인
                Diary diaryRecord = mDiaryDao.findByDate(dbDate);

                try {
                    holder.iv_item.setImageBitmap(getImageInBitmap(diaryRecord.getImage()));
                }
                catch (NullPointerException e ) { }
                holder.iv_item.setClipToOutline(true);

                if (position % 7 == 0) {
                    holder.tv_item.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.red));
                }

            } else {
                holder.isInMonth = false;
                holder.tv_item.setVisibility(View.INVISIBLE);
                holder.iv_item.setVisibility(View.INVISIBLE);

                //holder.tv_item.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.textColor_out));
                //holder.iv_item.setBackgroundColor(ContextCompat.getColor(MainActivity.context, R.color.bg_calendar_out));
                holder.iv_item.setImageBitmap(null);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 날짜 스트링 반환
    public String getDate(String day, boolean isDB) {
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));

        if (isDB == false)
            return mFormat.format(cal.getTime());
        else
            return dbFormat.format(cal.getTime());
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_item;
        ImageView iv_item;
        boolean isInMonth = true;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_item = itemView.findViewById(R.id.tv_date);
            iv_item = itemView.findViewById(R.id.iv_date);
            itemView.setOnClickListener(this);
        }

        // 각 아이템 클릭 리스너
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, (String) tv_item.getText(), isInMonth);
        }
    }

    // 클릭리스너 인터페이스
    public interface ItemClickListener {
        void onItemClick(View view, String day, boolean isInMonth);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
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