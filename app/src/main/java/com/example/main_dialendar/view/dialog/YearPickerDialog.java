package com.example.main_dialendar.view.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.main_dialendar.R;

import java.util.Calendar;

public class YearPickerDialog extends Dialog implements View.OnClickListener{

    private static final int MAX_YEAR = 2030;
    private static final int MIN_YEAR = 1980;

    private DatePickerDialog.OnDateSetListener listener;
    public Calendar cal = Calendar.getInstance();

    TextView btn_ok;
    NumberPicker yearPicker;
    NumberPicker monthPicker;

    public YearPickerDialog(@NonNull Context context) {
        super(context);
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_yearpicker);

        yearPicker = findViewById(R.id.picker_year);
        monthPicker = findViewById(R.id.picker_month);

        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        initPicker();
    }

    private void initPicker() {
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(cal.get(Calendar.YEAR));

    }

    @Override
    public void onClick(View view) {
        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue()-1, 0);
        this.cancel();
    }
}
