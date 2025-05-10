package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.Manifest;

import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private message_F message_f;

    FirebaseAuth mAuth;
    TextView lUser;
    FirebaseUser user;

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intentLogin = new Intent(getApplicationContext(),home.class);
//            startActivity(intentLogin);
//            finish();
//
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        CheckUserPermission();
        navigationButtons();
        userCheck();

        // SMS Permission
        ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.READ_SMS}, 1);

        // Firebase setup
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("messageData");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(Home.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(Home.this, SettingsActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(Home.this, profile.class));
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    private void addDataToFirebase(String phoneNumber, String message) {
        if (user == null) {
            Toast.makeText(Home.this, "User not logged in. Cannot save data.", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        message_F message_f = new message_F();
        message_f.setEmail(user.getEmail());
        message_f.setPhoneNumber(phoneNumber);
        message_f.setMessage(message);
        message_f.setDateTime(currentDateTime);

        databaseReference.push().setValue(message_f)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(Home.this, "Data added successfully!", Toast.LENGTH_SHORT).show()
                ).addOnFailureListener(e ->
                        Toast.makeText(Home.this, "Failed to add data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }


    void CheckUserPermission() {
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResutl) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResutl.length > 0 && grantResutl[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResutl);
        }
    }


    public void userCheck(){
        mAuth = FirebaseAuth.getInstance();
        lUser = findViewById(R.id.LogUserEmailHome);
        user = mAuth.getCurrentUser();

        if (user == null){
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        else {
            lUser.setText(user.getEmail());
        }
    }

    public void navigationButtons(){
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
                Intent callsIntent =  new Intent(getApplicationContext(), CallHistoryActivity.class);
                startActivity(callsIntent);
            }
        });

        //Message Button
        LinearLayout buttonMessages = findViewById(R.id.messages_home);
        buttonMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messageIntent = new Intent(getApplicationContext(), message_history.class);
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
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
    }
}