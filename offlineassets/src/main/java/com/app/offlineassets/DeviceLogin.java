
package com.app.offlineassets;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class DeviceLogin {

    @SerializedName("message")
    private String mMessage;
    @SerializedName("status")
    private Boolean mStatus;
    @SerializedName("token")
    private String mToken;

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

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

}
