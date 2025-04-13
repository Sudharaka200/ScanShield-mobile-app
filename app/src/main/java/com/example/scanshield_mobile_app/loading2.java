package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loading2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading2);

        //Testing- Logo2 Click
        ImageView logo2 = findViewById(R.id.imageView2);
        logo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logo2Click =  new Intent(getApplicationContext(), home.class);
                startActivity(logo2Click);
            }
        });




    }
}