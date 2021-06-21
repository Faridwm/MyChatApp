package com.fwmubarok.mychatapp.Model;

import com.google.gson.annotations.SerializedName;

public class Date {
    @SerializedName("addDate")
    private String addDate;

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }
}
