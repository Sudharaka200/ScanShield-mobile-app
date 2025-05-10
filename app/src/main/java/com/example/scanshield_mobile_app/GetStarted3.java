package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GetStarted3 extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_ONBOARDING_COMPLETED = "isOnboardingCompleted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started3);

        Button finishButton = findViewById(R.id.btnNext2);
        finishButton.setOnClickListener(v -> {
            // Mark onboarding as completed
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_ONBOARDING_COMPLETED, true);
            editor.apply();

            // Go to LoginActivity
            Intent intent = new Intent(GetStarted3.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close GetStarted3
        });
    }
}