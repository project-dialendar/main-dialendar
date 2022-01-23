package com.example.main_dialendar.view.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.main_dialendar.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

public class BackupDialog extends Dialog implements View.OnClickListener {

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
        switch (view.getId()) {
            case R.id.tv_create:
                createBackupFile();
                break;
            case R.id.tv_read:
                readBackupFile();
                break;
        }
    }

    // 백업 파일 생성
    private void createBackupFile() {
        TedPermission.with(getContext())
                .setPermissionListener(createPermissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    // 백업 파일 불러오기
    private void readBackupFile() {
        TedPermission.with(getContext())
                .setPermissionListener(readPermissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    PermissionListener createPermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            try {
                backupEachFile("/data/com.example.main_dialendar/databases/dialendar_db-shm", "/Download/dialendar_db-shm");
                backupEachFile("/data/com.example.main_dialendar/databases/dialendar_db-wal", "/Download/dialendar_db-wal");
                backupEachFile("/data/com.example.main_dialendar/databases/dialendar_db", "/Download/dialendar_db");
            } catch (Exception e) {
                Toast.makeText(getContext(), "백업 실패", Toast.LENGTH_LONG);
                Log.e("###", "backup failed");
                e.printStackTrace();
            }
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(getContext(), "접근이 거부되었습니다. 권한을 허용해주세요.", Toast.LENGTH_LONG);
        }
    };

    PermissionListener readPermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            try {
                restoreEachFile("/Download/dialendar_db-shm", "/data/com.example.main_dialendar/databases/dialendar_db-shm");
                restoreEachFile("/Download/dialendar_db-wal", "/data/com.example.main_dialendar/databases/dialendar_db-wal");
                restoreEachFile("/Download/dialendar_db", "/data/com.example.main_dialendar/databases/dialendar_db");
            } catch (Exception e) {
                Toast.makeText(getContext(), "복구 실패", Toast.LENGTH_LONG);
                Log.e("###", "restore failed");
                e.printStackTrace();
            }
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(getContext(), "접근이 거부되었습니다. 권한을 허용해주세요.", Toast.LENGTH_LONG);
        }
    };

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
            Toast.makeText(getContext(), from + "백업 성공", Toast.LENGTH_LONG).show();
            Log.i("###", from + "backup success");
            cancel();

        }
        else {
            Toast.makeText(getContext(), from + "백업 실패 in try", Toast.LENGTH_LONG).show();
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

            Toast.makeText(getContext(), from + ": 복구가 완료되었습니다.", Toast.LENGTH_LONG).show();
            Log.i("###", "restore success");
            cancel();
        }
        else {
            Toast.makeText(getContext(), from + ": 복구 실패 in try", Toast.LENGTH_LONG).show();
        }
    }
}
