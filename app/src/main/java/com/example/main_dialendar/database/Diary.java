package com.example.main_dialendar.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

// table name = "diary"
@Entity
@Getter
@Setter
public class Diary {

    /**
     * date 날짜 (주요키)
     * text 일기내용
     * image 일기사진
     * imageNullCheck 사진 존재 여부
     */
    @PrimaryKey
    @NonNull
    private String date;

    private String text;

    private byte[] image;

    public Diary() {
        date = "";
    }
}
