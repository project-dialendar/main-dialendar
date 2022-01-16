package com.example.main_dialendar.view.dialog;

import android.Manifest;
import android.app.Activity;
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
import com.example.main_dialendar.view.activity.MainActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

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
                createBackupFile();
                break;
            case R.id.tv_read:
                readBackupFile();
                break;
        }
    }

    // 백업 파일 생성
    private void createBackupFile() {
        //requestSignIn();
        TedPermission.with(getContext())
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    // 백업 파일 불러오기
    private void readBackupFile() {
        
    }

    // 데이터베이스 백업에 필요한 권한 요청
    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    File currentDB = new File(data, "/data/com.example.main_dialendar/databases/dialendar_db");
                    File backupDB = new File(sd, "/Download/dialendar.db");

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());

                    src.close();
                    dst.close();
                    Toast.makeText(getContext(), "백업 성공", Toast.LENGTH_LONG);
                    Log.i("###", "backup success");
                    cancel();

                }
                Toast.makeText(getContext(), "백업 실패 in try", Toast.LENGTH_LONG);
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

}
