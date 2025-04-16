package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class get_started_1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started1);

        //Getstarted Button
        Button buttonGetstarted =  findViewById(R.id.btnNext2);
        buttonGetstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getStartedBtn =  new Intent(getApplicationContext(), get_started2.class);
                startActivity(getStartedBtn);
            }
        });


    }
}