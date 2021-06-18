package com.fwmubarok.mychatapp.Model;

import com.google.gson.annotations.SerializedName;

public class SendResponse {

    @SerializedName("success")
    private int success;

    @SerializedName("failure")
    private int failure;

    private long message_id;

    public long getMessage_id() {
        return message_id;
    }

    public void setMessage_id(long message_id) {
        this.message_id = message_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }
}
