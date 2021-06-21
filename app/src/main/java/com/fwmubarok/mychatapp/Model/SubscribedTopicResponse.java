package com.fwmubarok.mychatapp.Model;

import com.google.gson.annotations.SerializedName;

public class SubscribedTopicResponse {
    @SerializedName("authorizedEntity")
    private String authorizedEntity;

    @SerializedName("rel")
    private Topics rel;

    public String getAuthorizedEntity() {
        return authorizedEntity;
    }

    public void setAuthorizedEntity(String authorizedEntity) {
        this.authorizedEntity = authorizedEntity;
    }

    public Topics getRel() {
        return rel;
    }

    public void setRel(Topics rel) {
        this.rel = rel;
    }
}
