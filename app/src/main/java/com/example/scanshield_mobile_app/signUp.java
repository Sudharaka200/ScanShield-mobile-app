package com.example.scanshield_mobile_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



public class signUp extends AppCompatActivity {

    TextView txtUsername, txtEmail, txtPhoneNumber, txtPassword, txtRetypePassword;
    Button butttonSignIn;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);



        mAuth = FirebaseAuth.getInstance();
        txtUsername = findViewById(R.id.usernameSignup);
        txtEmail = findViewById(R.id.emailSignup);
        txtPhoneNumber = findViewById(R.id.phoneNumberSignup);
        txtPassword = findViewById(R.id.passwordSignup);
        txtRetypePassword = findViewById(R.id.retypePasswordSignup);
        butttonSignIn = findViewById(R.id.btn_signup);

        butttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uUsername, uEmail, uPhoneNumber,uPassword, uRetypePassword;

                uUsername = String.valueOf(txtUsername.getText());
                uEmail = String.valueOf(txtEmail.getText());
                uPhoneNumber = String.valueOf(txtPhoneNumber.getText());
                uPassword = String.valueOf(txtPassword.getText());
                uRetypePassword = String.valueOf(txtRetypePassword.getText());

                if (TextUtils.isEmpty(uUsername)){
                    Toast.makeText(signUp.this,"Enter Username",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(uEmail)){
                    Toast.makeText(signUp.this,"Enter Email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(uPhoneNumber)){
                    Toast.makeText(signUp.this,"Enter Phonenumber",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!uPassword.equals(uRetypePassword)){
                    Toast.makeText(signUp.this,"Password doesn't match",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(uEmail, uPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("username", uUsername);
                                    userData.put("email", uEmail);
                                    userData.put("phonenumber", uPhoneNumber);
                                    userData.put("password", uPassword);

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("users").document(user.getUid())
                                            .set(userData)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(signUp.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                                Intent signUpIntent = new Intent(getApplicationContext(), login.class);
                                                startActivity(signUpIntent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(signUp.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                user.delete();
                                            });
                                } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(signUp.this, "Account Creation Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}