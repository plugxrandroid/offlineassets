
package com.app.offlineassets.Content;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Asset {

    @SerializedName("name")
    private String mName;
    @SerializedName("target_id")
    private String mTargetId;
    @SerializedName("updated_date")
    private Long mUpdatedDate;
    @SerializedName("url")
    private String mUrl;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTargetId() {
        return mTargetId;
    }

    public void setTargetId(String targetId) {
        mTargetId = targetId;
    }

    public Long getUpdatedDate() {
        return mUpdatedDate;
    }

    public void setUpdatedDate(Long updatedDate) {
        mUpdatedDate = updatedDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

}
