package com.example.main_dialendar.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.main_dialendar.R;

public class TimePickerDialog extends Dialog implements View.OnClickListener{

    public static int DEFAULT_HOUR = 20;
    public static int DEFAULT_MINUTE = 0;

    private android.app.TimePickerDialog.OnTimeSetListener listener;

    TextView tv_ok;
    TimePicker timePicker;

    public TimePickerDialog(@NonNull Context context) {
        super(context);
    }

    public void setListener(android.app.TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timepicker);

        timePicker = findViewById(R.id.timepicker);
        initPicker();

        tv_ok = findViewById(R.id.btn_ok);
        tv_ok.setOnClickListener(this);
    }

    private void initPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(DEFAULT_HOUR);
            timePicker.setMinute(DEFAULT_MINUTE);
        }
        timePicker.setIs24HourView(false);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        listener.onTimeSet(null, timePicker.getHour(), timePicker.getMinute());
        this.cancel();
    };
}
