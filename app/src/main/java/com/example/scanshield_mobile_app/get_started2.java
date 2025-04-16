package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class get_started2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started2);

        //Button Naviagation
         Button buttonNext = findViewById(R.id.btnNext2);
         buttonNext.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intentBtnNext = new Intent(getApplicationContext(), get_started3.class);
                 startActivity(intentBtnNext);
             }
         });
    }
}