package com.app.offlineassets;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button book1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button button = (Button)findViewById(R.id.book1);


        // Get App Content
        //downloadAssets.GetOfflineContent("PJYS7P");

        // Check Book is downloaded or not
        // boolean isBookActivated = downloadAssets.GetBookActivationStatus("Marvel");

        //if (isBookActivated == true){

        // If book is activated download assets here
        //downloadAssets.DownloadAssets("Marvel","PJYS7P");
               /* }else {
                    // If book is not, activate your Folder
                    // Popup Dialogue for activate Book Api
                    // Your Activation Code UI is here
                    //Toast.makeText(getApplicationContext(),"Book is Not Activated",Toast.LENGTH_SHORT).show();

                    // Activate Keys Here
                    // Send Activation Key, olderName, Project Id
                    downloadAssets.activateKey("abcdef","Marvel","PJYS7P");

                    // Check key activation status
                    if (downloadAssets.isActivated()){
                        // If key is activated your code is here
                    }else {
                        // If key is not activated your code is here
                    }




                }*/

        // Initialize Offline Version
        final PlugxrOffline downloadAssets = new PlugxrOffline(MainActivity.this);

        // Run Time Permissions, Permissions are mandatory
        Dexter.withActivity(MainActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {





                // Authenticate Offline Data
                downloadAssets.Authenticate("PJYWC6","Animals");


                final Handler myHandler = new Handler();
                final int delay = 1000; // 1000 milliseconds == 1 second


                Toast.makeText(getApplicationContext(),"Finally",Toast.LENGTH_LONG).show();

                /*myHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (downloadAssets.isDownloaded() == true){
                            Toast.makeText(getApplicationContext(),"Finally",Toast.LENGTH_LONG).show();
                        }
                        //myHandler.postDelayed(this, delay);
                    }
                }, delay);*/






            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {  }
        }).check();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String targetId =  downloadAssets.getTargetId("Animals","anim30");

                Toast.makeText(getApplicationContext(),targetId,Toast.LENGTH_LONG).show();
            }
        });


    }
}