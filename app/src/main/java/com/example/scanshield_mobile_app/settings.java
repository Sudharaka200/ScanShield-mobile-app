package com.example.scanshield_mobile_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class settings extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(settings.this, home.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(settings.this, profile.class));
                    return true;
                } else {
                    return false;
                }
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
    }
}
