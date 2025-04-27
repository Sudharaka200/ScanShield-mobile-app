package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;import android.Manifest;


public class blocked_numbers extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView lUser;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blocked_numbers);

        userCheck();

    }

    public void userCheck(){
        mAuth = FirebaseAuth.getInstance();
        lUser = findViewById(R.id.LogUserEmailHome);
        user = mAuth.getCurrentUser();

        if (user == null){

        }
        else {
            lUser.setText(user.getEmail());
        }
    }



}