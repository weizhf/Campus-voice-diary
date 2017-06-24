package com.group.android.finalproject.player.util;

/**
 * Created by YZQ on 2016/12/20.
 */

public class RecordItem {
    private String title, date, feel, place, remark, storeUrl;

    public RecordItem(String title, String date, String feel, String place, String remark, String storeUrl) {
        this.title = title;
        this.date = date;
        this.feel = feel;
        this.place = place;
        this.remark = remark;
        this.storeUrl = storeUrl;
    }

    public String getDate() {
        return date;
    }

    public String getFeel() {
        return feel;
    }

    public String getPlace() {
        return place;
    }

    public String getRemark() {
        return remark;
    }

    public String getStoreUrl() {
        return storeUrl;
    }

    public String getTitle() {
        return title;
    }

    public void update(String title, String date, String feel, String place, String remark) {
        this.title = title;
        this.date = date;
        this.feel = feel;
        this.place = place;
        this.remark = remark;
    }
}
