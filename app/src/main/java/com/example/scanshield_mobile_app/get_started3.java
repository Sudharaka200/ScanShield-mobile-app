package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class get_started3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started3);

        Button buttonGetStarted = findViewById(R.id.btnNext2);
        buttonGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent geStartedIntent = new Intent(getApplicationContext(), login.class);
                startActivity(geStartedIntent);
            }
        });


    }
}