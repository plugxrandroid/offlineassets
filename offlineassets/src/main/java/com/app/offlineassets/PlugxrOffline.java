package com.app.offlineassets;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.ArrayList;
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

public class PlugxrOffline extends AppCompatActivity {

    private static Context context;
    private static List<String> assetsDataList = new ArrayList<>();
    private List<Data> folderData;
    private ProgressDialog dialog;

    public PlugxrOffline(Context context) {
        this.context = context;
    }


    // Login Access
    public void Authenticate(final String projectId, final String folderName){



            final TinyDB tinyDB = new TinyDB(context);



            // Android device key
            String android_id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            dialog = new ProgressDialog(context);
            dialog.setMessage("Please Wait...");
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.progressdialog);
            dialog.setCancelable(false);

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
                            dialog.dismiss();
                        }
                    }

                }

                @Override
                public void onFailure(Call<DeviceLogin> call, Throwable t) {
                    dialog.dismiss();
                }
            });
        }



    public void GetOfflineContent(final String projectId, final String folderName){

        final TinyDB tinyDB = new TinyDB(context);

        Log.v("Plugxr",projectId);
        Log.v("Plugxr",folderName);

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



                                Log.v("Plugxr","Response : "+response.body());

                                dialog.dismiss();

                                DownloadAssetsData(folderName,projectId,folderData);



                                //DownloadAssets(folderName,projectId);

                            }


                        }
                    }else {
                         dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<Folder> call, Throwable t) {
                      dialog.dismiss();
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

                                    Log.v("Plugxr","Second Time If Date is Not Same: "+folderData);

                             //       DownloadAssets(folderName,projectId);

                                    dialog.dismiss();

                                    DownloadAssetsData(folderName,projectId, folderData);

                                }else {
                                    tinyDB.getString("FoldersData");
                                    try {
                                        JSONArray jsonArray = new JSONArray(tinyDB.getString("FoldersData"));

                                        Log.v("Plugxr","Second Time If Date is Same: "+jsonArray);
                                        dialog.dismiss();
                                        DownloadAssetsData(folderName,projectId, folderData);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                  //  DownloadAssets(folderName,projectId);

                                }

                            }else {
                                dialog.dismiss();
                            }
                        }else {
                               dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Folder> call, Throwable t) {
                         dialog.dismiss();
                    }
                });
            }else {

                dialog.dismiss();

                tinyDB.getString("FoldersData");
                try {
                    JSONArray jsonArray = new JSONArray(tinyDB.getString("FoldersData"));
                         Log.v("Plugxr","Second Time : "+jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.v("Plugxr","Already Downloaded");

                /*File projectPath = new File(context.getExternalFilesDir(null) + "/"+projectId);
                if (projectPath.exists()){
                    File folderPath = new File(context.getExternalFilesDir(null) + "/"+projectId+"/"+folderName);
                    if (folderPath.exists()){

                    }else {
                        DownloadAssetsData(folderName,projectId);
                    }
                }else {
                    DownloadAssetsData(folderName,projectId);
                }*/

                // Log.v("Plugxr",new Gson().toJson(tinyDB.getString("FoldersData")));

            }
        }



    }


    // Download assets
    private static void DownloadAssetsData(final String folderName, String projectName, List<Data> folderData){



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

        Log.v("PRIYA","Folder Data is There"+checkFolderData);

        // Check weather entered folder is available or not.
        if (checkFolderData == true){
            // Check data is updated or not
            String folderDate = tinyDB.getString(folderName+"UpdateDate");
            boolean checkFolderUpdate = isFolderUpdateAvailable(folderName,jsonArray,folderDate,folderData);

            Log.v("PRIYA","Folder Update is Available"+checkFolderUpdate);


            if (checkFolderUpdate == true){
                downloadDataSet(folderName,jsonArray,projectName);
            }else {
                Log.v("PRIYA","Folder is Available"+checkFolderUpdate);
                // Check weather entered folder is available or not.
                DownloadAssetsData(folderName,jsonArray,projectName);
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



                    JSONArray jsonArrayAssets = jsonObject.getJSONArray("assets");

                    // if (folderActivated == true){

                    boolean isDownloaded = isFolderisAvailableinDevice(projectName,folderName);

                    Log.v("PRIYA","IS PROJECT IS AVAILABLE : "+isDownloaded);

                    //Is Folder is Downloaded or Not
                    if (isDownloaded == false){
                        File projectPath = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/"+projectName);
                        if (!projectPath.exists()){
                            projectPath.mkdir();
                        }

                        File folderPath = new File(context.getExternalFilesDir(null).getAbsolutePath() +"/"+projectName+"/"+folderName);
                        if (!folderPath.exists()){
                            folderPath.mkdir();
                        }

                        Log.v("Plugxr",folderUrl);
                        // Start Download process
                        if (!folderUrl.equals("")){
                            DownloadFolder(folderPath,folderUrl,folderName,projectName,jsonArray);
                        }


                        /*}else {
                            // If book is already downloaded
                            Log.v("Plugxr","Book Downloaded");
                        }*/

                    }else {
                        // If book not activate
                        Log.v("Plugxr","Book Not Activated");
                        DownloadAssetsData(folderName,jsonArray,projectName);
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
                        zipArchive.unzip(folderPath+"/"+folderName+".zip", String.valueOf(folderPath+"/"+"Dataset"),"");

                        Log.v("SURYA","FINAL PATH : "+folderPath);



                        File file = new File(folderPath+"/"+folderName+".zip");
                        if (file.isFile()){
                            file.delete();
                        }


                        Toast.makeText(context,"FINAL PATH : "+folderPath+"/"+"Dataset",Toast.LENGTH_SHORT).show();



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





                    }

                    @Override
                    public void onError(Error error) {

                    }


                });

    }



    private static void DownloadAssetsData(String folderName, JSONArray jsonArray, String projectName) {


        File assetsPath = new File(context.getExternalFilesDir(null).getAbsolutePath() +"/"+projectName+"/"+folderName+"/"+"Assets");
        if (!assetsPath.exists()){
            assetsPath.mkdir();
        }


        Log.v("RAMJI","JSON DATA :  "+jsonArray);

        Log.v("RAMJI",projectName+" : "+folderName);


        for (int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String folderStr = jsonObject.getString("folder_name");



                // Check given Folder is Available or not
                if (folderStr.equals(folderName)){
                    //Check update is available or not.

                    JSONArray jsonArrayAssets = jsonObject.getJSONArray("assets");


                    TinyDB tinyDB = new TinyDB(context);

                    //if (tinyDB.getString(folderName).equals("")){


                        for (int j = 0;j<jsonArrayAssets.length();j++) {
                            JSONObject jsonObjectAssets = jsonArrayAssets.getJSONObject(j);
                            String assetsDateStr = jsonObjectAssets.getString("updated_date");
                            String assetfolderName = jsonObjectAssets.getString("target_id");
                            String assetsUrl = jsonObjectAssets.getString("url");

                            Log.v("SAHA","Asset Url : "+assetsUrl);

                            //assetsDataList.add(assetsDateStr);

                            if (tinyDB.getString(folderName).equals("")){

                                if (!assetsUrl.equals("")){

                                    Log.v("SAHA","Asset Url1 : "+assetsUrl);

                                    DownloadAssetsFiles(assetsPath,assetsUrl,assetfolderName,projectName,jsonArray);
                                }


                            }else {
                                JSONArray jsonAssetsArrayTiny = null;
                                try {
                                    jsonAssetsArrayTiny = new JSONArray(tinyDB.getString(folderName));


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (jsonAssetsArrayTiny.equals("")){
                                    if (!assetsDateStr.equals(jsonAssetsArrayTiny.getJSONObject(j).getString("updated_date").toString())){

                                        Log.v("Plugxr","Folder Udated : "+assetsDateStr.equals(jsonAssetsArrayTiny.getJSONObject(j).getString("updated_date")));
                                        if (!assetsUrl.equals("")){

                                            Log.v("SAHA","Asset Url2 : "+assetsUrl);

                                            DownloadAssetsFiles(assetsPath,assetsUrl,assetfolderName,projectName,jsonArray);
                                        }
                                    }
                                }else {
                                    if (!assetsUrl.equals("")){

                                        Log.v("SAHA","Asset Url3 : "+assetsUrl);

                                        DownloadAssetsFiles(assetsPath,assetsUrl,assetfolderName,projectName,jsonArray);
                                    }
                                }


                                /*else {
                                    Log.v("Plugxr","Folder Udated : "+assetsDateStr.equals(jsonAssetsArrayTiny.getJSONObject(j).getString("updated_date")));
                                    if (!assetsUrl.equals("")){

                                        Log.v("SAHA","Asset Url3 : "+assetsUrl);

                                        DownloadAssetsFiles(assetsPath,assetsUrl,assetfolderName,projectName,jsonArray);
                                    }
                                }*/
                            }



                        }




                    }

                /*else {


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
                    }*/







            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private static void DownloadAssetsFiles(final File assetsPath, final String folderUrl, final String assetfolderName, final String projectName, final JSONArray jsonArray) {


        int downloadId = PRDownloader.download(folderUrl, String.valueOf(assetsPath), assetfolderName+".zip")
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

                        Log.v("SURYA","ASSETS COMPLETED");

                        ZipArchive zipArchive = new ZipArchive();
                        zipArchive.unzip(assetsPath+"/"+assetfolderName+".zip", String.valueOf(assetsPath+"/"+assetfolderName),"");


                        Log.v("SURYA","ASSETS PATH : "+assetsPath+"/"+assetfolderName+".zip");

                        File file = new File(assetsPath+"/"+assetfolderName+".zip");
                        if (file.isFile()){
                            file.delete();
                        }


                        Toast.makeText(context,"Downloaded",Toast.LENGTH_SHORT).show();




                    }

                    @Override
                    public void onError(Error error) {

                    }


                });
    }


    private static boolean isFolderisAvailableinDevice(String projectName, String folderName) {

        boolean status = false;

        File projectPath = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/"+projectName);


        Log.v("PRIYA","Project Path "+projectPath);

        if(projectPath.exists()) {
            File folderPath = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/"+projectName+"/"+folderName);
            Log.v("PRIYA","Folder Path "+folderPath);

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


    private static boolean isFolderUpdateAvailable(String folderName, JSONArray jsonArray, String folderDate, List<Data> folderData) {

        boolean status = false;

        TinyDB tinyDB = new TinyDB(context);

        for (int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String folderStr = jsonObject.getString("folder_name");
                // Check given Folder is Available or not
                if (folderStr.equals(folderName)){
                    Log.v("PRIYA",folderName);

                    //Check update is available or not.
                    String folderDateStr = jsonObject.getString("updated_date");




                    if (folderDate.equals("")){

                        status = true;
                        tinyDB.putString(folderName,folderData.get(i).getAssets().toString());
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

    private static boolean isDataAvailable(String folderName, JSONArray jsonArray) {

        Log.v("Plugxr",folderName);

        boolean status = false;

        for (int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String folderStr = jsonObject.getString("folder_name");

                Log.v("Plugxr",jsonObject.getString("folder_name"));
                if (folderStr.equals(folderName)){

                    Log.v("Plugxr",jsonObject.getString("folder_name"));
                    status = true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return status;
    }


    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    }

