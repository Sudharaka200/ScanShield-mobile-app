package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Dailpad button
        LinearLayout buttonDialPad = findViewById(R.id.dialpad_home);
        buttonDialPad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialpadIntent = new Intent(getApplicationContext(), dialpad.class);
                startActivity(dialpadIntent);
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
        LinearLayout buttonMesssages =  findViewById(R.id.message_home);
        buttonMesssages.setOnClickListener(new View.OnClickListener() {
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




    }
}