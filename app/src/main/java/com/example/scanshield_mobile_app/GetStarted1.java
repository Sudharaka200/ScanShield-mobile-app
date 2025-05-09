package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GetStarted1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started1);

        Button getStartedButton = findViewById(R.id.btnNext2);
        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(GetStarted1.this, GetStarted2.class);
            startActivity(intent);
            finish(); // Close GetStarted1
        });
    }
}