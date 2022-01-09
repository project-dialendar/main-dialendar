package com.example.main_dialendar.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main_dialendar.R;
import com.example.main_dialendar.view.activity.MediaScanner;

import java.io.File;
import java.io.FileOutputStream;

public class CaptureDialog {
    private Context context;
    private static final String TAG = "DialogActivity";
    public CaptureDialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수 정의
    public void callCaptureDialog(String date, Bitmap diary_image, String diary_text) {

        // 다이얼로그 호출
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_capture);
        dialog.show();
        Log.i(TAG,"캡쳐 다이얼로그 호출 성공");

        final LinearLayout capture_target_Layout = (LinearLayout) dialog.findViewById(R.id.capture_target_Layout);
        final TextView tv_capture_date = (TextView) dialog.findViewById(R.id.tv_capture_date);
        final ImageView iv_captrue_image = (ImageView) dialog.findViewById(R.id.iv_captrue_image);
        final TextView tv_capture_diary = (TextView) dialog.findViewById(R.id.tv_capture_diary);

        final TextView btn_capture_cancel = (TextView) dialog.findViewById(R.id.btn_capture_cancel);
        final TextView btn_capture_this = (TextView) dialog.findViewById(R.id.btn_capture_this);

        String title = date;

        tv_capture_date.setText(date);
        iv_captrue_image.setImageBitmap(diary_image);
        tv_capture_diary.setText(diary_text);

        btn_capture_this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // '일기 저장' 버튼
                Request_Capture(capture_target_Layout, title);
                Toast.makeText(context,"일기를 저장했습니다.",Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그 종료
                dialog.dismiss();
            }
        });

        btn_capture_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // '캡쳐 취소' 버튼
                Toast.makeText(context,"캡쳐를 취소했습니다.",Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그 종료
                dialog.dismiss();
            }
        });

    }


    /**
     * 부분 영역 캡쳐 메소드
     * @param view 갭쳐할 뷰 지정
     * @param filename 저장힐 파일 이름
     */
    public void Request_Capture(View view, String filename){
        if(view==null){ //Null Point Exception ERROR 방지
            System.out.println("::::ERROR:::: view == NULL");
            return;
        }

        /* 캡쳐 파일 저장 */
        view.buildDrawingCache(); //캐시 비트 맵 만들기
        Bitmap bitmap = view.getDrawingCache();
        FileOutputStream fos;

        /* 저장할 폴더 Setting */
        File uploadFolder = Environment.getExternalStoragePublicDirectory("/DCIM/Camera/"); //저장 경로 (File Type형 변수)

        if (!uploadFolder.exists()) { //만약 경로에 폴더가 없다면
            uploadFolder.mkdir(); //폴더 생성
        }

        /* 파일 저장 */
        String Str_Path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/"; //저장 경로 (String Type 변수)

        try{
            fos = new FileOutputStream(Str_Path+filename+".jpg"); // 경로 + 제목 + .jpg로 FileOutputStream Setting
            bitmap.compress(Bitmap.CompressFormat.JPEG,80,fos);
        }catch (Exception e){
            e.printStackTrace();
        }

        //캡쳐 파일 미디어 스캔 (https://hongdroid.tistory.com/7)
        MediaScanner ms = MediaScanner.newInstance(context);

        try { // TODO : 미디어 스캔
            ms.mediaScanning(Str_Path + filename + ".jpg");
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("::::ERROR:::: "+e);
        }

    }
}


