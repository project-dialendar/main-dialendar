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

    /* 삽입 */
    @Insert
    void insertDiary(Diary diary);

    /* 수정 */
    @Update
    void updateDiary(Diary diary);

    /* 삭제 */
    @Delete
    void deleteDiary(Diary diary);

    /* 조회 */
    @Query("SELECT * FROM Diary WHERE date LIKE :date")
    Diary findByDate(String date);
}
