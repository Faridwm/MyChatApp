package com.fwmubarok.mychatapp.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Topics {
    @SerializedName("topics")
    private Map<String, Date> topics;

    public Map<String, Date> getTopics() {
        return topics;
    }

    public void setTopics(Map<String, Date> topics) {
        this.topics = topics;
    }
}
