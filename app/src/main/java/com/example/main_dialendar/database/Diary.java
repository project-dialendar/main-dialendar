package com.example.main_dialendar.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// table name = "diary"
@Entity
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

    private int imageNullCheck; // [null : 0], [image exist : 1]

    public Diary() {
        date = "";
        imageNullCheck = 0;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getImageNullCheck() {
        return imageNullCheck;
    }

    public void setImageNullCheck(int imageNullCheck) {
        this.imageNullCheck = imageNullCheck;
    }
}
