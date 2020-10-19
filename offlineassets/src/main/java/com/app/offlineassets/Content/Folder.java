
package com.app.offlineassets.Content;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Folder {

    @SerializedName("data")
    private List<Data> mData;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("status")
    private Boolean mStatus;
    @SerializedName("updated_date")
    private String mUpdatedDate;

    public List<Data> getData() {
        return mData;
    }

    public void setData(List<Data> data) {
        mData = data;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Boolean getStatus() {
        return mStatus;
    }

    public void setStatus(Boolean status) {
        mStatus = status;
    }

    public String getUpdatedDate() {
        return mUpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        mUpdatedDate = updatedDate;
    }

}
