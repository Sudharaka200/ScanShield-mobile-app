package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_successfully extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView lUser;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_successfully);

        navigationButton();
        userCheck();

    }

    public void userCheck(){

        mAuth = FirebaseAuth.getInstance();
        lUser = findViewById(R.id.LogUserEmail);
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

    public void navigationButton(){
        //Button Home
        Button buttonHome =  findViewById(R.id.btn_login_successfully);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBtnHome =  new Intent(getApplicationContext(), Home.class);
                startActivity(intentBtnHome);
            }
        });

    }
}