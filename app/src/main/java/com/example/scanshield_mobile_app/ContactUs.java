package com.example.scanshield_mobile_app;
import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ContactUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // Initialize UI elements
        TextView websiteLink = findViewById(R.id.website_link);
        TextView gmailLink = findViewById(R.id.gmail_link);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up website link click listener
        websiteLink.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.scanshield.lk"));
            startActivity(browserIntent);
        });

        // Set up email link click listener
        gmailLink.setOnClickListener(v -> {
            // Primary intent: ACTION_SENDTO with mailto
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:getsupport@scanshield@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
                Log.d(TAG, "Email intent (SENDTO) started");
            } else {
                Log.e(TAG, "No email client found for SENDTO, trying ACTION_SEND");
                // Fallback intent: ACTION_SEND
                Intent fallbackIntent = new Intent(Intent.ACTION_SEND);
                fallbackIntent.setType("message/rfc822");
                fallbackIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"scanshieldmobileapp@gmail.com"});   //getsupport@scanshield@gmail.com
                fallbackIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
                if (fallbackIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(fallbackIntent);
                    Log.d(TAG, "Email intent (SEND) started");
                } else {
                    Toast.makeText(this, "No email client installed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "No email client found for SEND");
                }
            }
        });



        // Prevent any item from being pre-selected
        bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }

        // Set the item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(ContactUs.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(ContactUs.this, SettingsActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(ContactUs.this, profile.class));
                    return true;
                }
                return false;
            }
        });
    }
}