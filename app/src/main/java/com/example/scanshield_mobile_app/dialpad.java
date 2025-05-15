package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.Manifest;



public class dialpad extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView logUser;
    FirebaseUser user;

    static int PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dialpad);

        dialPadButtons();
        userCheck();
        getDialedNumberForCall();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Prevent any item from being pre-selected
        bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }

        // Set the item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(dialpad.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(dialpad.this, SettingsActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(dialpad.this, profile.class));
                    return true;
                }
                return false;
            }
        });

    }

    public void userCheck(){

        mAuth = FirebaseAuth.getInstance();
        logUser = findViewById(R.id.logUserEmailDialPad);
        user = mAuth.getCurrentUser();

        if (user == null){

        }else {
            logUser.setText(user.getEmail());
        }

    }

    public void dialPadButtons(){
        //DialPad Setup
        TextView numberDisplay = findViewById(R.id.Entered_NumbersCall);
        Button btn1 =  findViewById(R.id.NumButton1);
        Button btn2 = findViewById(R.id.NumButton2);
        Button btn3 = findViewById(R.id.NumButton3);
        Button btn4 = findViewById(R.id.NumButton4);
        Button btn5 = findViewById(R.id.NumButton5);
        Button btn6 = findViewById(R.id.NumButton6);
        Button btn7 = findViewById(R.id.NumButton7);
        Button btn8 = findViewById(R.id.NumButton8);
        Button btn9 = findViewById(R.id.NumButton9);
        Button btn10 = findViewById(R.id.NumButton10);
        Button btn11 = findViewById(R.id.NumButton11);
        Button btn12 = findViewById(R.id.NumButton12);

        final StringBuilder numberBuilder = new StringBuilder();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("1");
                numberDisplay.setText(numberBuilder.toString());
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("2");
                numberDisplay.setText(numberBuilder.toString());
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("3");
                numberDisplay.setText(numberBuilder.toString());
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("4");
                numberDisplay.setText(numberBuilder.toString());
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("5");
                numberDisplay.setText(numberBuilder.toString());
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("6");
                numberDisplay.setText(numberBuilder.toString());
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("7");
                numberDisplay.setText(numberBuilder.toString());
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("8");
                numberDisplay.setText(numberBuilder.toString());
            }
        });

        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("9");
                numberDisplay.setText(numberBuilder.toString());
            }
        });

        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("*");
                numberDisplay.setText(numberBuilder.toString());
            }
        });

        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("0");
                numberDisplay.setText(numberBuilder.toString());
            }
        });

        btn12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberBuilder.append("#");
                numberDisplay.setText(numberBuilder.toString());
            }
        });
    }

    public void getDialedNumberForCall(){
        TextView phoneNumberTxt = findViewById(R.id.Entered_NumbersCall);
        ImageView callBtn = findViewById(R.id.callButtonNumber);

        if (ContextCompat.checkSelfPermission(dialpad.this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(dialpad.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = phoneNumberTxt.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(callIntent);

            }
        });

    }
}