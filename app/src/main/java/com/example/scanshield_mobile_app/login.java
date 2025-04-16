package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //button Login
        Button buttonLogin =  findViewById(R.id.btn_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBtnLogin =  new Intent(getApplicationContext(), login_successfully.class);
                startActivity(intentBtnLogin);
            }
        });

        //Button Withoutlogin
        Button buttonWithoutLogin =  findViewById(R.id.btn_Wlogin);
        buttonWithoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBtnWithoutLogin =  new Intent(getApplicationContext(), home.class);
                startActivity(intentBtnWithoutLogin);
            }
        });


    }
}