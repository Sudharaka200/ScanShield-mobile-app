package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loading2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading2);

        //Loading Delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent LogosecondIntent = new Intent(loading2.this, get_started_1.class);
                startActivity(LogosecondIntent);
                finish();
            }
        },2000);
    }
}