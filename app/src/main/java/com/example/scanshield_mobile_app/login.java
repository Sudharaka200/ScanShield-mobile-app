package com.example.scanshield_mobile_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intentLogin = new Intent(getApplicationContext(),login_successfully.class);
            startActivity(intentLogin);
            finish();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        navigationButtons();

        mAuth = FirebaseAuth.getInstance();
        txtEmail = findViewById(R.id.emailLogin);
        txtPassword = findViewById(R.id.passwordLogin);
        buttonLogin = findViewById(R.id.btn_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uEmail, uPassword;
                uEmail = String.valueOf(txtEmail.getText());
                uPassword = String.valueOf(txtPassword.getText());

                if (TextUtils.isEmpty(uEmail)){
                    Toast.makeText(login.this,"Enter Email",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(uPassword)){
                    Toast.makeText(login.this,"Enter Password",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(uEmail, uPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intentLogin = new Intent(getApplicationContext(),login_successfully.class);
                                    startActivity(intentLogin);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });




    }













    public void navigationButtons(){

        //button signup
        TextView buttonSignUp = findViewById(R.id.signup_txt);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBtnSignUp = new Intent(getApplicationContext(), signUp.class);
                startActivity(intentBtnSignUp);
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