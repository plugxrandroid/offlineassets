
package com.app.offlineassets.Content;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Data {

    @SerializedName("assets")
    private List<Asset> mAssets;
    @SerializedName("folder_id")
    private Long mFolderId;
    @SerializedName("folder_name")
    private String mFolderName;
    @SerializedName("is_activated")
    private Boolean mIsActivated;
    @SerializedName("updated_date")
    private Long mUpdatedDate;
    @SerializedName("zip_url")
    private String mZipUrl;

    public List<Asset> getAssets() {
        return mAssets;
    }

    public void setAssets(List<Asset> assets) {
        mAssets = assets;
    }

    public Long getFolderId() {
        return mFolderId;
    }

    public void setFolderId(Long folderId) {
        mFolderId = folderId;
    }

    public String getFolderName() {
        return mFolderName;
    }

    public void setFolderName(String folderName) {
        mFolderName = folderName;
    }

    public Boolean getIsActivated() {
        return mIsActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        mIsActivated = isActivated;
    }

    public Long getUpdatedDate() {
        return mUpdatedDate;
    }

    public void setUpdatedDate(Long updatedDate) {
        mUpdatedDate = updatedDate;
    }

    public String getZipUrl() {
        return mZipUrl;
    }

    public void setZipUrl(String zipUrl) {
        mZipUrl = zipUrl;
    }

}
