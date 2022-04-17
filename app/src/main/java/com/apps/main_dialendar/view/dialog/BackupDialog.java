package com.apps.main_dialendar.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apps.main_dialendar.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class BackupDialog extends Dialog implements View.OnClickListener {

    private static final String URL_APP_DB = "/data/com.example.main_dialendar/databases/dialendar_db";
    private static final String URL_APP_SHM = "/data/com.example.main_dialendar/databases/dialendar_db-shm";
    private static final String URL_APP_WAL = "/data/com.example.main_dialendar/databases/dialendar_db-wal";
    private static final String URL_LOCAL_DB = "/Download/dialendar_db";
    private static final String URL_LOCAL_SHM = "/Download/dialendar_db-shm";
    private static final String URL_LOCAL_WAL = "/Download/dialendar_db-wal";
    private static final String TAG = "BackupDialog";

    TextView tv_create;
    TextView tv_read;

    public BackupDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_backup);

        tv_create = findViewById(R.id.btn_create);
        tv_create.setOnClickListener(this);
        tv_read = findViewById(R.id.btn_read);
        tv_read.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                createBackupFile();
                break;
            case R.id.btn_read:
                readBackupFile();
                break;
        }
    }

    // 백업 파일 생성
    private void createBackupFile() {
        try {
            backupEachFile(URL_APP_SHM, URL_LOCAL_SHM);
            backupEachFile(URL_APP_WAL, URL_LOCAL_WAL);
            backupEachFile(URL_APP_DB, URL_LOCAL_DB);
        } catch (Exception e) {
            Toast.makeText(getContext(), getContext().getString(R.string.backup_failed), Toast.LENGTH_LONG);
            Log.e(TAG, "backup failed");
            e.printStackTrace();
        }
    }

    // 백업 파일 불러오기
    private void readBackupFile() {
        try {
            restoreEachFile(URL_LOCAL_SHM, URL_APP_SHM);
            restoreEachFile(URL_LOCAL_WAL, URL_APP_WAL);
            restoreEachFile(URL_LOCAL_DB, URL_APP_DB);
        } catch (Exception e) {
            Toast.makeText(getContext(), getContext().getString(R.string.restore_failed), Toast.LENGTH_LONG);
            Log.e(TAG, "restore failed");
            e.printStackTrace();
        }
    }

    private void backupEachFile(String from, String to) throws IOException {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        if (sd.canWrite()) {
            File currentDB = new File(data, from);
            File backupDB = new File(sd, to);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());

            src.close();
            dst.close();
            Toast.makeText(getContext(), getContext().getString(R.string.backup_success), Toast.LENGTH_LONG).show();
            Log.i(TAG, from + "backup success");
            cancel();

        }
        else {
            Toast.makeText(getContext(), getContext().getString(R.string.backup_authorization_failed), Toast.LENGTH_LONG).show();
        }
    }

    private void restoreEachFile(String from, String to) throws IOException {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        if (sd.canWrite()) {
            File currentDB = new File(sd, from);
            File restoreDB = new File(data, to);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(restoreDB).getChannel();
            dst.transferFrom(src, 0, src.size());

            src.close();
            dst.close();

            Toast.makeText(getContext(), getContext().getString(R.string.restore_success), Toast.LENGTH_LONG).show();
            Log.i(TAG, "restore success");
            cancel();
        }
        else {
            Toast.makeText(getContext(), getContext().getString(R.string.restore_authorization_failed), Toast.LENGTH_LONG).show();
        }
    }
}
