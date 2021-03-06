package com.app.offlineassets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.offlineassets.Content.Data;
import com.app.offlineassets.Content.Folder;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ir.mahdi.mzip.zip.ZipArchive;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadAssets extends AppCompatActivity {

    private static Context context;
    private static List<Data> folderData;
    private static List<String> assetsDataList;
    private Boolean mKeyStatus = false;

    public DownloadAssets(Context context) {
        this.context = context;
    }


    // Login Access
    public static void Authenticate(final String projectId, final String folderName){



        final TinyDB tinyDB = new TinyDB(context);

        boolean loginStatus = tinyDB.getBoolean("islogin");

        if (loginStatus == false){

            // Android device key
            String android_id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            /*final ProgressDialog dialog = new ProgressDialog(context);
            dialog.setMessage("Please Wait...");
            dialog.show();*/

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Api.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Api api = retrofit.create(Api.class);

            Call<DeviceLogin> call = api.getDeviceToken(android_id);

            call.enqueue(new Callback<DeviceLogin>() {
                @Override
                public void onResponse(Call<DeviceLogin> call, Response<DeviceLogin> response) {



                    if (response.body()!=null){
                        DeviceLogin mobileOtp = response.body();
                        boolean status = mobileOtp.getStatus();
                        if (status == true){

                            tinyDB.putString("token",mobileOtp.getToken());
                            tinyDB.putBoolean("islogin",true);

                            GetOfflineContent(projectId,folderName);

                            Log.v("Plugxr",mobileOtp.getToken());

                        //    dialog.dismiss();
                        }else {
                    //        dialog.dismiss();
                        }
                    }

                }

                @Override
                public void onFailure(Call<DeviceLogin> call, Throwable t) {
              //      dialog.dismiss();
                }
            });
        }


    }


    public static void GetOfflineContent(final String projectId, final String folderName){

        final TinyDB tinyDB = new TinyDB(context);


        if (tinyDB.getBoolean("checkFirstTime") == false){

            /*final ProgressDialog dialog = new ProgressDialog(context);
            dialog.setMessage("Please Wait...");
            dialog.show();*/




            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    okhttp3.Request newRequest  = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + tinyDB.getString("token"))
                            .addHeader("Accept", "application/json")
                            .build();
                    return chain.proceed(newRequest);
                }
            }).connectTimeout(1000, TimeUnit.SECONDS)
                    .writeTimeout(1000,TimeUnit.SECONDS)
                    .readTimeout(1000,TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true).build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Api.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Api api = retrofit.create(Api.class);

            Call<Folder> call = api.getContent(projectId);

            call.enqueue(new Callback<Folder>() {
                @Override
                public void onResponse(Call<Folder> call, Response<Folder> response) {
                    if (response.isSuccessful()){

                      //  dialog.dismiss();

                        Folder folder = response.body();


                        boolean status = folder.getStatus();

                        tinyDB.putBoolean("checkFirstTime",status);

                        if(status == true){

                            TinyDB tinyDB = new TinyDB(context);

                            String projectPpdateDate = folder.getUpdatedDate();
                            if (!projectPpdateDate.equals(tinyDB.getString("ProjectUpdatedDate"))){

                                tinyDB.putString("ProjectUpdatedDate",projectPpdateDate);

                                folderData = folder.getData();

                                tinyDB.putString("FoldersData",new Gson().toJson(folderData));


                                DownloadAssets(folderName,projectId);

                            }else {

                            }



                        }
                    }else {
                       // dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<Folder> call, Throwable t) {
                  //  dialog.dismiss();
                }
            });
        }else {
            if (isNetworkAvailable()){
               /* final ProgressDialog dialog = new ProgressDialog(context);
                dialog.setMessage("Please Wait...");
                dialog.show();*/




                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Request newRequest  = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + tinyDB.getString("token"))
                                .addHeader("Accept", "application/json")
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).connectTimeout(1000, TimeUnit.SECONDS)
                        .writeTimeout(1000,TimeUnit.SECONDS)
                        .readTimeout(1000,TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true).build();


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Api.BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Api api = retrofit.create(Api.class);

                Call<Folder> call = api.getContent(projectId);

                call.enqueue(new Callback<Folder>() {
                    @Override
                    public void onResponse(Call<Folder> call, Response<Folder> response) {
                        if (response.isSuccessful()){

                           // dialog.dismiss();

                            Folder folder = response.body();


                            boolean status = folder.getStatus();


                            tinyDB.putBoolean("checkFirstTime",status);

                            if(status == true){



                                TinyDB tinyDB = new TinyDB(context);

                                String projectPpdateDate = folder.getUpdatedDate();
                                if (!projectPpdateDate.equals(tinyDB.getString("ProjectUpdatedDate"))){
                                    tinyDB.putString("ProjectUpdatedDate",projectPpdateDate);

                                    folderData = folder.getData();

                                    tinyDB.putString("FoldersData",new Gson().toJson(folderData));


                                    DownloadAssets(folderName,projectId);

                                }else {
                                    tinyDB.getString("FoldersData");
                                    try {
                                        JSONArray jsonArray = new JSONArray(tinyDB.getString("FoldersData"));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    DownloadAssets(folderName,projectId);

                                }

                            }
                        }else {
                         //   dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Folder> call, Throwable t) {
                       // dialog.dismiss();
                    }
                });
            }else {

                tinyDB.getString("FoldersData");
                try {
                    JSONArray jsonArray = new JSONArray(tinyDB.getString("FoldersData"));
               //     Log.v("Plugxr","Second Time : "+jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



               // Log.v("Plugxr",new Gson().toJson(tinyDB.getString("FoldersData")));

            }
        }







    }

    // Download assets
    public static void DownloadAssets(final String folderName, String projectName){



        Log.v("RAM","CAME TO DOWNLOAD ASSETS");

        TinyDB tinyDB = new TinyDB(context);
        tinyDB.getString("FoldersData");


        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(tinyDB.getString("FoldersData"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Check Folder Data is available or not

        boolean checkFolderData = isDataAvailable(folderName,jsonArray);


        // Check weather entered folder is available or not.
        if (checkFolderData == true){
            // Check data is updated or not
            String folderDate = tinyDB.getString(folderName+"UpdateDate");
            boolean checkFolderUpdate = isFolderUpdateAvailable(folderName,jsonArray,folderDate);


            if (checkFolderUpdate == true){
                downloadDataSet(folderName,jsonArray,projectName);
            }else {
            }
        }else {
            Toast.makeText(context,"Folder is eampty please try with another folder",Toast.LENGTH_SHORT).show();
        }

    }

    private static void downloadDataSet(String folderName, JSONArray jsonArray, String projectName) {

        for (int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String folderStr = jsonObject.getString("folder_name");
                String folderDateStr = jsonObject.getString("updated_date");
                TinyDB tinyDB = new TinyDB(context);
                tinyDB.putString(folderName+"UpdateDate",folderDateStr);

                // Check given Folder is Available or not
                if (folderStr.equals(folderName)){
                    Boolean folderActivated = jsonObject.getBoolean("is_activated");
                    String folderUrl = jsonObject.getString("zip_url");

                   // if (folderActivated == true){

                        boolean isDownloaded = isFolderisAvailableinDevice(projectName,folderName);


                        //Is Folder is Downloaded or Not
                        if (isDownloaded == false){
                            File projectPath = new File(Environment.getExternalStorageDirectory() + "/"+projectName);
                            if (!projectPath.exists()){
                                projectPath.mkdir();
                            }

                            File folderPath = new File(Environment.getExternalStorageDirectory() +"/"+projectName+"/"+folderName);
                            if (!folderPath.exists()){
                                folderPath.mkdir();
                            }

                            // Start Download process
                            DownloadFolder(folderPath,folderUrl,folderName,projectName,jsonArray);

                        /*}else {
                            // If book is already downloaded
                            Log.v("Plugxr","Book Downloaded");
                        }*/

                    }else {
                        // If book not activate
                        Log.v("Plugxr","Book Not Activated");
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private static void DownloadFolder(final File folderPath, final String folderUrl, final String folderName, final String projectName, final JSONArray jsonArray) {

        Log.v("Plugxr",folderPath.toString());

        int downloadId = PRDownloader.download(folderUrl, String.valueOf(folderPath), folderName+".zip")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                        Log.v("SURYA","DATA : "+progress.currentBytes);




                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        Log.v("SURYA","COMPLETED");

                        Log.v("SURYA","PATH : "+folderPath);


                        ZipArchive zipArchive = new ZipArchive();
                        zipArchive.unzip(folderPath+"/"+folderName+".zip", String.valueOf(folderPath),"");

                        Log.v("SURYA","FINAL PATH : "+folderPath);



                        File file = new File(folderPath+"/"+folderName+".zip");
                        if (file.isFile()){
                            file.delete();
                        }


                        Toast.makeText(context,"FINAL PATH : "+folderPath+"/"+folderName,Toast.LENGTH_SHORT).show();



                        // Download Assets Content

                        TinyDB tinyDB = new TinyDB(context);
                        tinyDB.getString("FoldersData");
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(tinyDB.getString("FoldersData"));
                            Log.v("Plugxr","Download Assets : "+jsonArray);
                            Log.v("Plugxr","Folder Name : "+folderName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Check weather entered folder is available or not.

                        DownloadAssetsData(folderName,jsonArray,projectName);


                      //  DownloadAssetsFiles(folderPath,folderUrl,folderName,projectName, jsonArray);




                    }

                    @Override
                    public void onError(Error error) {

                    }


                });

    }

    private static void DownloadAssetsFiles(final File folderPath, final String folderUrl, final String assetfolderName, final String projectName, final JSONArray jsonArray) {


        int downloadId = PRDownloader.download(folderUrl, String.valueOf(folderPath), assetfolderName+".zip")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                        Log.v("SURYA","DATA : "+progress.currentBytes);




                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        Log.v("SURYA","COMPLETED");

                        Log.v("SURYA","PATH : "+folderPath);


                        ZipArchive zipArchive = new ZipArchive();
                        zipArchive.unzip(folderPath+"/"+assetfolderName+".zip", String.valueOf(folderPath),"");

                        Log.v("SURYA","FINAL PATH : "+folderPath);



                        File file = new File(folderPath+"/"+assetfolderName+".zip");
                        if (file.isFile()){
                            file.delete();
                        }


                        //Toast.makeText(context,"FINAL PATH : "+folderPath+"/"+folderName,Toast.LENGTH_SHORT).show();




                    }

                    @Override
                    public void onError(Error error) {

                    }


                });
    }

    private static boolean isFolderisAvailableinDevice(String projectName, String folderName) {

        boolean status = false;

        File projectPath = new File(Environment.getExternalStorageDirectory() + "/"+projectName);
        if (!projectPath.exists()){
            projectPath.mkdir();
        }

        if(projectPath.exists()) {
            File folderPath = new File(Environment.getExternalStorageDirectory() + "/"+projectName+"/"+folderName);
            if (folderPath.exists()){
                status = true;
            }else {
                status = false;
            }

        }else {
            status = false;
        }

        return status;
    }

    private static boolean isFolderUpdateAvailable(String folderName, JSONArray jsonArray, String folderDate) {

        boolean status = false;

        for (int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String folderStr = jsonObject.getString("folder_name");
                // Check given Folder is Available or not
                if (folderStr.equals(folderName)){
                    Log.v("SURYA",folderName);

                    //Check update is available or not.
                    String folderDateStr = jsonObject.getString("updated_date");


                    if (folderDate.equals("")){

                        status = true;

                    }else {
                        if (folderDateStr.equals(folderDate)){
                            status = false;
                        }else {
                            status = true;
                        }
                    }





                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return status;
    }


    private static void DownloadAssetsData(String folderName, JSONArray jsonArray, String projectName) {


        File assetsPath = new File(Environment.getExternalStorageDirectory() +"/"+projectName+"/"+"Assets");
        if (!assetsPath.exists()){
            assetsPath.mkdir();
        }

        for (int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String folderStr = jsonObject.getString("folder_name");
                // Check given Folder is Available or not
                if (folderStr.equals(folderName)){
                    //Check update is available or not.

                    JSONArray jsonArrayAssets = jsonObject.getJSONArray("assets");

                    for (int j = 0;j<jsonArrayAssets.length();j++){
                        JSONObject jsonObjectAssets = jsonArray.getJSONObject(i);
                        String assetsDateStr = jsonObjectAssets.getString("updated_date");
                        String assetsUrl = jsonObjectAssets.getString("url");
                        String assetfolderName = jsonObjectAssets.getString("target_id");

                        if (assetsDataList.size() == 0){

                            assetsDataList.add(assetsDateStr);

                            TinyDB tinyDB = new TinyDB(context);
                            tinyDB.putString(folderName,jsonArrayAssets.toString());






                            if (!assetsUrl.equals("")){
                                DownloadAssetsFiles(assetsPath,assetsUrl,assetfolderName,projectName,jsonArray);
                            }


                        }else {

                            TinyDB tinyDB = new TinyDB(context);
                            tinyDB.getString("FoldersData");
                            JSONArray jsonAssetsArray = null;
                            try {
                                jsonAssetsArray = new JSONArray(tinyDB.getString(folderName));
                                Log.v("Plugxr","Download Assets : "+jsonAssetsArray);
                                Log.v("Plugxr","Folder Name : "+folderName);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (!assetsDateStr.equals(jsonAssetsArray.getJSONObject(j).getString("updated_date"))){
                                if (!assetsUrl.equals("")){
                                    DownloadAssetsFiles(assetsPath,assetsUrl,assetfolderName,projectName,jsonArray);
                                }
                            }
                        }

                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private static boolean isDataAvailable(String folderName, JSONArray jsonArray) {

        boolean status = false;

        for (int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String folderStr = jsonObject.getString("folder_name");

                if (folderStr.equals(folderName)){
                    status = true;
                }else {
                    status = false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return status;
    }


    // Open Facebook Messenger
    public static void Toast(String message){

     Toast.makeText(context,
             message,
                    Toast.LENGTH_SHORT).show();

    }


    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean GetBookActivationStatus(String folderName) {


        boolean status = false;

        TinyDB tinyDB = new TinyDB(context);
        tinyDB.getString("FoldersData");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(tinyDB.getString("FoldersData"));
            Log.v("Plugxr","Download Assets : "+jsonArray);
            Log.v("Plugxr","Folder Name : "+folderName);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String folderStr = jsonObject.getString("folder_name");

                // Check given Folder is Available or not
                if (folderStr.equals(folderName)){
                    Boolean folderActivated = jsonObject.getBoolean("is_activated");

                    if (folderActivated == true){

                        status = true;

                    }else {
                        status = false;
                        Log.v("Plugxr","Book Not Activated");
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return status;
    }

    public void activateKey(String activation_key, final String folderName, final String projectName) {


        final TinyDB tinyDB = new TinyDB(context);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + tinyDB.getString("token"))
                        .addHeader("Accept", "application/json")
                        .build();
                return chain.proceed(newRequest);
            }
        }).connectTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000,TimeUnit.SECONDS)
                .readTimeout(1000,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true).build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        Call<Folder> call = api.getContent(activation_key);

        call.enqueue(new Callback<Folder>() {
            @Override
            public void onResponse(Call<Folder> call, Response<Folder> response) {

                if (response.isSuccessful()){

                    if (response.body().getStatus() == true){
                        setActivated(true);
                        GetOfflineContent(projectName, folderName);
                        Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                    }else {
                        setActivated(false);
                        Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }else {
                    setActivated(false);
                    Toast.makeText(context,"Error : Please try again.",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Folder> call, Throwable t) {
                setActivated(false);
                Toast.makeText(context,"Error : Please try again.",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public Boolean isActivated() {
        return mKeyStatus;
    }

    public void setActivated(Boolean status) {
        mKeyStatus = status;
    }

}
