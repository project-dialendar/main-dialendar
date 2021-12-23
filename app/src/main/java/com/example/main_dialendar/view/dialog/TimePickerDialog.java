package com.example.main_dialendar.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.example.main_dialendar.R;

public class TimePickerDialog extends Dialog {
    private Context context;

    TextView tv_ok;
    TimePicker timePicker;

    public TimePickerDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timepicker);

        tv_ok = findViewById(R.id.btn_ok);
        timePicker = findViewById(R.id.timepicker);

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
