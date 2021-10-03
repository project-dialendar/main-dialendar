package com.example.main_dialendar.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object
 */
@Dao
public interface DiaryDao {

    /**
     * 삽입
     * @param diary 저장할 다이어리 인스턴스
     */
    @Insert
    void insertDiary(Diary diary);

    /**
     * 수정
     * @param diary 수정할 다이어리 인스턴스
     */
    @Update
    void updateDiary(Diary diary);

    /**
     * 삭제
     * @param diary 삭제할 다이어리 인스턴스
     */
    @Delete
    void deleteDiary(Diary diary);

    /**
     * 조회
     * @param date 조회할 날짜 스트링
     * @return 다이어리 인스턴스
     */
    @Query("SELECT * FROM Diary WHERE date LIKE :date")
    Diary findByDate(String date);
}
