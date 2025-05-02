package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;

import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class call_history extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView logedUser;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call_history);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(call_history.this, home.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(call_history.this, settings.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(call_history.this, profile.class));
                    return true;
                } else {
                    return false;
                }
            }
        });


        userCheck();

    }

    public void userCheck(){
        mAuth = FirebaseAuth.getInstance();
        logedUser = findViewById(R.id.loginUserEmailCallHistory);
        user = mAuth.getCurrentUser();

        if (user == null){

        }else {
            logedUser.setText(user.getEmail());
        }

    }
}