package com.example.scanshield_mobile_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class profile extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Check if user is logged in
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (!prefs.getBoolean(KEY_IS_LOGGED_IN, false) || currentUser == null) {
            Log.d("SessionCheck", "Redirecting to LoginActivity: isLoggedIn=" + prefs.getBoolean(KEY_IS_LOGGED_IN, false) + ", currentUser=" + (currentUser == null));
            Intent intent = new Intent(this, loginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Set user email in TextView
        TextView emailTextView = findViewById(R.id.logedUserEmailProfile);
        emailTextView.setText(currentUser.getEmail());

        // Initialize Logout Button
        Button logoutButton = findViewById(R.id.btn_Wlogout); // Corrected to match profile_layout.xml
        logoutButton.setOnClickListener(v -> {
            // Save user email and mark as logged out
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_EMAIL, currentUser.getEmail());
            editor.putBoolean(KEY_IS_LOGGED_IN, false);
            editor.apply();

            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Navigate to LoginActivity and clear back stack
            Intent intent = new Intent(profile.this, loginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(profile.this, home.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(profile.this, settings.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    return true; // Already on profile
                } else {
                    return false;
                }
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }
}