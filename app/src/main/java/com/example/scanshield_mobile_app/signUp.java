package com.example.scanshield_mobile_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signUp extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String TAG = "SignUpActivity";

    EditText txtUsername, txtEmail, txtPhoneNumber, txtPassword, txtRetypePassword;
    Button buttonSignUp;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (currentUser != null && prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            Intent intent = new Intent(getApplicationContext(), home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        txtUsername = findViewById(R.id.usernameSignup);
        txtEmail = findViewById(R.id.emailSignup);
        txtPhoneNumber = findViewById(R.id.phoneNumberSignup);
        txtPassword = findViewById(R.id.passwordSignup);
        txtRetypePassword = findViewById(R.id.retypePasswordSignup);
        buttonSignUp = findViewById(R.id.btn_signup);

        buttonSignUp.setOnClickListener(v -> {
            String uUsername = txtUsername.getText().toString().trim();
            String uEmail = txtEmail.getText().toString().trim();
            String uPhoneNumber = txtPhoneNumber.getText().toString().trim();
            String uPassword = txtPassword.getText().toString().trim();
            String uRetypePassword = txtRetypePassword.getText().toString().trim();

            if (TextUtils.isEmpty(uUsername)) {
                Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(uEmail)) {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(uPhoneNumber)) {
                Toast.makeText(this, "Enter Phonenumber", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!uPassword.equals(uRetypePassword)) {
                Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(uEmail, uPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Save email to SharedPreferences
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(KEY_EMAIL, uEmail);
                            editor.putBoolean(KEY_IS_LOGGED_IN, false); // User must log in
                            editor.apply();

                            // Save user data to Firestore
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("username", uUsername);
                            userData.put("email", uEmail);
                            userData.put("phonenumber", uPhoneNumber);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(user.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "User data saved successfully for UID: " + user.getUid());
                                        // Show success dialog
                                        new AlertDialog.Builder(this)
                                                .setTitle("Signup Successful")
                                                .setMessage("Your account has been created successfully. Please log in to continue.")
                                                .setPositiveButton("OK", (dialog, which) -> {
                                                    Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .setCancelable(false)
                                                .show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to save user data", e);
                                        Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        // Do not delete user; allow login
                                        // Navigate to LoginActivity even if Firestore fails
                                        new AlertDialog.Builder(this)
                                                .setTitle("Signup Successful")
                                                .setMessage("Your account has been created, but user data could not be saved. Please log in to continue.")
                                                .setPositiveButton("OK", (dialog, which) -> {
                                                    Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .setCancelable(false)
                                                .show();
                                    });

                            // Navigate to LoginActivity regardless of Firestore outcome
                            Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Account creation failed", task.getException());
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(this, "Account Creation Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}