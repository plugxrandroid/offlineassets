package com.app.offlineassets;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DownloadAssets extends AppCompatActivity {

    private static Context context;

    public DownloadAssets(Context context) {
        this.context = context;
    }

    // Open Facebook Messenger
    public static void Toast(String message){

     Toast.makeText(context,
             message,
                    Toast.LENGTH_SHORT).show();

    }


}
