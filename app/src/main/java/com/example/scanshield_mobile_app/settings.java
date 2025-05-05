package com.example.scanshield_mobile_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class settings extends Activity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String TAG = "SettingsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //check user logged in
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (!prefs.getBoolean(KEY_IS_LOGGED_IN, false) || currentUser == null) {
            Log.d(TAG, "Redirecting to LoginActivity: isLoggedIn=" + prefs.getBoolean(KEY_IS_LOGGED_IN, false) + ", currentUser=" + (currentUser == null));
            Intent intent = new Intent(this, loginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Optionally set user email in TextView (currently shows "Settings")
        TextView emailTextView = findViewById(R.id.LogUserEmailHome);

        // Initialize settings items
        Switch notificationsSwitch = findViewById(R.id.switch_notifications);
        Switch darkModeSwitch = findViewById(R.id.switch_darkmode);
        TextView detectionTypes = findViewById(R.id.detection_types);
        TextView blacklistManagement = findViewById(R.id.blacklist_management);
        TextView deleteAccount = findViewById(R.id.delete_account);

        // Set click listeners for placeholder settings
//        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            Toast.makeText(this, "Notifications " + (isChecked ? "enabled" : "disabled") + " (feature not implemented)", Toast.LENGTH_SHORT).show();
//        });
//
//        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            Toast.makeText(this, "Dark Mode " + (isChecked ? "enabled" : "disabled") + " (feature not implemented)", Toast.LENGTH_SHORT).show();
//        });
//
//        detectionTypes.setOnClickListener(v -> {
//            Toast.makeText(this, "Detection Types clicked (feature not implemented)", Toast.LENGTH_SHORT).show();
//        });
//
//        blacklistManagement.setOnClickListener(v -> {
//            Toast.makeText(this, "Blacklist Management clicked (feature not implemented)", Toast.LENGTH_SHORT).show();
//        });

        // Delete Account functionality
        deleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to permanently delete your account? This action cannot be undone.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = user.getUid();
                        String userEmail = user.getEmail();

                        // Prompt for password to re-authenticate
                        EditText passwordInput = new EditText(this);
                        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        new AlertDialog.Builder(this)
                                .setTitle("Re-authenticate")
                                .setMessage("Please enter your password to confirm account deletion.")
                                .setView(passwordInput)
                                .setPositiveButton("Confirm", (d, w) -> {
                                    String password = passwordInput.getText().toString();
                                    if (password.isEmpty()) {
                                        Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    // Re-authenticate user
                                    AuthCredential credential = EmailAuthProvider.getCredential(userEmail, password);
                                    user.reauthenticate(credential)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d(TAG, "Re-authentication successful");
                                                deleteRealtimeDatabaseData(user, userId, prefs);
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "Re-authentication failed", e);
                                                new AlertDialog.Builder(this)
                                                        .setTitle("Error")
                                                        .setMessage("Re-authentication failed: " + e.getMessage())
                                                        .setPositiveButton("OK", null)
                                                        .setCancelable(true)
                                                        .show();
                                            });
                                })
                                .setNegativeButton("Cancel", null)
                                .setCancelable(true)
                                .show();
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(true)
                    .show();
        });


        //bottom navigation menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(settings.this, home.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(settings.this, profile.class));
                    return true;
                } else {
                    return false;
                }
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
    }

    private void deleteRealtimeDatabaseData(FirebaseUser user, String userId, SharedPreferences prefs) {
        Log.d(TAG, "Attempting to delete Realtime Database data for UID: " + userId);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userId);
        db.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Realtime Database user data deleted for UID: " + userId);
                    deleteAuthUser(user, prefs);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete Realtime Database user data", e);
                    String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown Realtime Database error";
                    new AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Failed to delete user data from Realtime Database: " + errorMessage + ". Proceeding to delete authentication data.")
                            .setPositiveButton("OK", (d, w) -> deleteAuthUser(user, prefs))
                            .setCancelable(true)
                            .show();
                });
    }

    private void deleteAuthUser(FirebaseUser user, SharedPreferences prefs) {
        Log.d(TAG, "Attempting to delete Firebase Authentication user");
        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase Authentication user deleted");
                        // Clear SharedPreferences
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.apply();

                        // Show success dialog
                        new AlertDialog.Builder(this)
                                .setTitle("Account Deleted")
                                .setMessage("Your account has been successfully deleted.")
                                .setPositiveButton("OK", (d, w) -> {
                                    Intent intent = new Intent(this, loginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .setCancelable(false)
                                .show();
                    } else {
                        Log.e(TAG, "Failed to delete Firebase Authentication user", task.getException());
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        new AlertDialog.Builder(this)
                                .setTitle("Error")
                                .setMessage("Failed to delete account: " + errorMessage)
                                .setPositiveButton("OK", null)
                                .setCancelable(true)
                                .show();
                    }
                });
    }
}

