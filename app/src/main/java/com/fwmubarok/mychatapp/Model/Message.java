package com.fwmubarok.mychatapp.Model;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private final Map<String, String> data;
    private final String topic;
    private final String token;

    public Map<String, String> getData() {
        return data;
    }

    public String getTopic() {
        return topic;
    }

    public String getToken() {
        return token;
    }


    private Message(Builder builder) {
        this.data = builder.data.isEmpty() ? null : ImmutableMap.copyOf(builder.data);
        this.token = builder.token;
        this.topic = builder.topic;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, String> data = new HashMap<>();
        private String topic;
        private String token;

        public Builder() {}

        public Builder putData(@NonNull String key, @NonNull String value) {
            this.data.put(key, value);
            return this;
        }

        public Builder putAllData(@NonNull Map<String, String> map) {
            this.data.putAll(map);
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
