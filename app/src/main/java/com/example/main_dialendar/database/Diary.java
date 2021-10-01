package com.example.main_dialendar.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// table name = "diary"
@Entity
public class Diary {

    @PrimaryKey
    private String date;

    private String text;

    private byte[] imageView;

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

    public byte[] getImageView() {
        return imageView;
    }

    public void setImageView(byte[] imageView) {
        this.imageView = imageView;
    }
}
