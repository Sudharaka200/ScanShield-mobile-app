package com.example.scanshield_mobile_app;

import static android.content.ContentValues.TAG;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    EditText txtEmail, txtPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        txtEmail = findViewById(R.id.emailLogin);
        txtPassword = findViewById(R.id.passwordLogin);
        buttonLogin = findViewById(R.id.btn_login);

        // Load saved email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedEmail = prefs.getString(KEY_EMAIL, "");
        if (!savedEmail.isEmpty()) {
            txtEmail.setText(savedEmail);
        }

        // Handle login button click
        buttonLogin.setOnClickListener(v -> {
            String uEmail = txtEmail.getText().toString().trim();
            String uPassword = txtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(uEmail)) {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(uPassword)) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(uEmail, uPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Save login info
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(KEY_EMAIL, uEmail);
                            editor.putBoolean(KEY_IS_LOGGED_IN, true);
                            editor.apply();

                            new AlertDialog.Builder(this)
                                    .setTitle("Login Successful")
                                    .setMessage("You have logged in successfully.")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        Intent intent = new Intent(getApplicationContext(), home.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .setCancelable(false)
                                    .show();

                            Intent intent = new Intent(getApplicationContext(), login_successfully.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            String errorMessage = task.getException().getMessage();
                            // Show error dialog
                            new AlertDialog.Builder(this)
                                    .setTitle("Login Failed")
                                    .setMessage("Authentication failed: " + errorMessage)
                                    .setPositiveButton("OK", null)
                                    .setCancelable(true)
                                    .show();
                        }
                    });
        });

        // Handle signup navigation
        TextView buttonSignUp = findViewById(R.id.signup_txt);
        buttonSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), signUp.class);
            startActivity(intent);
        });
    }
}