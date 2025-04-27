package com.example.scanshield_mobile_app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class message_history_test extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView logedUser;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message_history_test);

        userCheck();

    }

    public void userCheck(){

        mAuth = FirebaseAuth.getInstance();
        logedUser = findViewById(R.id.logUserEmailMessageHistory);
        user = mAuth.getCurrentUser();

        if (user == null){

        }else {
            logedUser.setText(user.getEmail());
        }

    }
}