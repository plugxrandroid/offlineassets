package com.app.offlineassets;

import com.app.offlineassets.Content.Asset;
import com.app.offlineassets.Content.Folder;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {
    static String BASE_URL = "https://we.plugxr.com/";


    @POST("api/v3/deviceLogin")
    @FormUrlEncoded
    Call<DeviceLogin> getDeviceToken(@Field("device_id") String device_id);


    @GET("api/v3/OfflineAppContent/{projectId}")
    Call<Folder> getContent(@Path("projectId") String projectId);


}
