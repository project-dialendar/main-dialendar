package com.apps.main_dialendar.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Diary.class}, version = 3, exportSchema = false)
public abstract class DiaryDatabase extends RoomDatabase {

    public abstract DiaryDao diaryDao();

    private static DiaryDatabase instance = null;

    /**
     * 데이터베이스 싱글톤
     * (추상클래스에선 생성자를 private하게 만들 필요 없음.)
     * @param context 애플리케이션 컨텍스트
     * @return 데이터베이스 인스턴스
     */
    public static synchronized DiaryDatabase getInstance(Context context) {
        if (instance == null) {
            instance= Room.databaseBuilder(
                    context.getApplicationContext(),
                    DiaryDatabase.class,
                    "dialendar_db")                   // db name :스키마
                    .fallbackToDestructiveMigration()       // 스키마(database) 버전 변경 가능
                    .allowMainThreadQueries()               // Main Thread에서 DB의 IO(입출력)를 가능하게 함. 이건 혼자 만들고 실행해볼때.
                    .build();
        }
        return instance;
    }
}
