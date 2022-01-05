package com.example.main_dialendar.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.main_dialendar.R;

public class BackupDialog extends Dialog implements View.OnClickListener{

    TextView tv_create;
    TextView tv_read;

    public BackupDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_backup);

        tv_create = findViewById(R.id.tv_create);
        tv_create.setOnClickListener(this);
        tv_read = findViewById(R.id.tv_read);
        tv_read.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.tv_create:
                break;
            case R.id.tv_read:
                break;
        }
    }
}
