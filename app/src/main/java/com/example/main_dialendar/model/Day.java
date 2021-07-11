package com.example.main_dialendar.model;

import android.media.Image;

/**
 * 캘린더의 날짜 정보를 저장하는 클래스
 */
public class Day {
    private String day;
    private boolean inMonth;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isInMonth() {
        return inMonth;
    }

    public void setInMonth(boolean inMonth) {
        this.inMonth = inMonth;
    }
}
