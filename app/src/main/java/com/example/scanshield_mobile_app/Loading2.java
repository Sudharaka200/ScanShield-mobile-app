package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class Loading2 extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_ONBOARDING_COMPLETED = "isOnboardingCompleted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading2);

        // Check if onboarding is completed
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isOnboardingCompleted = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false);

        // Transition after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (isOnboardingCompleted) {
                intent = new Intent(Loading2.this, Home.class);
            } else {
                intent = new Intent(Loading2.this, GetStarted1.class);
            }
            startActivity(intent);
            finish(); // Close Loading2 activity
        }, 2000); // 2000ms = 2 seconds
    }
}