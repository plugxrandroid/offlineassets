package com.app.offlineassets;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadAssets downloadAssets = new DownloadAssets(MainActivity.this);

        downloadAssets.Toast("Toast Came");

    }
}