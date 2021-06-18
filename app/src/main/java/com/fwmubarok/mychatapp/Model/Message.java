package com.fwmubarok.mychatapp.Model;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Message {
    @SerializedName("to")
    private String to;

    @SerializedName("data")
    private Map<String, String> data;

    @SerializedName("notification")
    private Notification notification;

    public Message(String to, Map<String, String> data, Notification notification) {
        this.to = "/topics/" + to;
        this.data = data;
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = "/topics/" + to;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
