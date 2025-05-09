package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GetStarted2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started2);

        Button nextButton = findViewById(R.id.btnNext2);
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(GetStarted2.this, GetStarted3.class);
            startActivity(intent);
            finish(); // Close GetStarted2
        });
    }
}