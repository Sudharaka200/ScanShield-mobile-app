package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.Manifest;

import android.Manifest;



import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        //SMS Permission
        ActivityCompat.requestPermissions(home.this, new String[]{Manifest.permission.READ_SMS}, 1);
        ActivityCompat.requestPermissions(home.this, new String[]{Manifest.permission.RECEIVE_SMS}, 2);

        //Call Permission
        ActivityCompat.requestPermissions(home.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 3);
        ActivityCompat.requestPermissions(home.this, new String[]{Manifest.permission.READ_CALL_LOG}, 4);
        ActivityCompat.requestPermissions(home.this, new String[]{Manifest.permission.CALL_PHONE}, 5);



















        // Dailpad button
        LinearLayout buttonDialPad = findViewById(R.id.dialpad_home);
        buttonDialPad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialpadIntent = new Intent(getApplicationContext(), dialpad.class);
                startActivity(dialpadIntent);
            }
        });

        //Search Button
        LinearLayout buttonSearch = findViewById(R.id.serach_home);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(getApplicationContext(), number_search.class);
                startActivity(searchIntent);
            }
        });

        // call button
        LinearLayout buttonCalls = findViewById(R.id.calls_home);
        buttonCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callsIntent =  new Intent(getApplicationContext(), call_history.class);
                startActivity(callsIntent);
            }
        });

        //Message Button
        LinearLayout buttonMessages = findViewById(R.id.messages_home);
        buttonMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messageIntent = new Intent(getApplicationContext(), message_history_test.class);
                startActivity(messageIntent);
            }
        });

        //Blocked Calls Button
        LinearLayout buttonBlocked = findViewById(R.id.blocked_home);
        buttonBlocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent blockecCallsIntent = new Intent(getApplicationContext(), blocked_numbers.class);
                startActivity(blockecCallsIntent);
            }
        });

        //Spam Messages Button
        LinearLayout buttonSpam = findViewById(R.id.spam_home);
        buttonSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent spamIntent = new Intent(getApplicationContext(), spam_messages.class);
                startActivity(spamIntent);
            }
        });

        //settings Button
        LinearLayout buttonSettings = findViewById(R.id.setting_home);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(getApplicationContext(), profile.class);
                startActivity(settingsIntent);
            }
        });
        
        //NavigationBar
//        NavigationView navigationView = findViewById(R.id.bottom_navigation);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int id = item.getItemId();
//
//                if (id == R.id.nav_home2){
//                    startActivity(new Intent(getApplicationContext(),home.class));
//                } else if (id == R.id.setting_home) {
//                    startActivity(new Intent(getApplicationContext(), protection.class));
//                } else if (id == R.id.nav_profile3){
//                    startActivity(new Intent(getApplicationContext(), profile.class));
//                }
//
//                DrawerLayout drawerLayout = findViewById(R.id.bottom_navigation);
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return false;
//            }
//        });

//        TextView txtMessage;
//        TextView txtMessageTime;
//
//        txtMessage = findViewById(R.id.textMessages);
//        ActivityCompat.requestPermissions(home.this, new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

    }

//    public void Read_SMS(View view){
//
//        Cursor cursor = getContentResolver().query(Uri.parse("contenr://name"))
//
//    }
}