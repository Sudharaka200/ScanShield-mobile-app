package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Loading delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent LogoIntent = new Intent(getApplicationContext(), Loading2.class);
                startActivity(LogoIntent);
                finish();
            }
        }, 2000);

    }
}

