package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.Manifest;

import android.Manifest;
import android.widget.Toast;


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

        CheckUserPermission();

        //SMS Permission
        ActivityCompat.requestPermissions(home.this, new String[]{Manifest.permission.READ_SMS}, 1);

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

    }

    void CheckUserPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                        != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS
            }, REQUEST_CODE_ASK_PERMISSIONS);
        }

    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResutl){
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResutl[0] == PackageManager.PERMISSION_GRANTED){

                }else {
                    Toast.makeText(this,"denailed" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResutl);
        }
    }
}