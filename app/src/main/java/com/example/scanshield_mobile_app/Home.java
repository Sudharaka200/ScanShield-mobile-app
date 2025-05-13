package com.example.scanshield_mobile_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity {
    private static final String TAG = "Home";
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private TextView lUser;
    private FirebaseUser user;
    private ListView spamCallsListView;
    private ArrayList<String> spamCallsList;
    private ArrayAdapter<String> spamCallsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Firebase setup
        firebaseDatabase = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("messageData");

        // Initialize UI components
        spamCallsListView = findViewById(R.id.spam_calls_list);
        spamCallsList = new ArrayList<>();
        spamCallsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spamCallsList);
        spamCallsListView.setAdapter(spamCallsAdapter);

        // Initialize UI and navigation
        userCheck();
        navigationButtons();
        setupBottomNavigation();

        // Load blocked calls
        loadBlockedCalls();

        // Request permissions if needed
        checkUserPermission();
    }

    private void userCheck() {
        mAuth = FirebaseAuth.getInstance();
        lUser = findViewById(R.id.LogUserEmailHome);
        user = mAuth.getCurrentUser();

        if (user == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            lUser.setText(user.getEmail());
        }
    }

    private void navigationButtons() {
        LinearLayout buttonDialPad = findViewById(R.id.dialpad_home);
        buttonDialPad.setOnClickListener(v -> startActivity(new Intent(this, dialpad.class)));

        LinearLayout buttonSearch = findViewById(R.id.search_home);
        buttonSearch.setOnClickListener(v -> startActivity(new Intent(this, number_search.class)));

        LinearLayout buttonCalls = findViewById(R.id.calls_home);
        buttonCalls.setOnClickListener(v -> startActivity(new Intent(this, CallHistoryActivity.class)));

        LinearLayout buttonMessages = findViewById(R.id.messages_home);
        buttonMessages.setOnClickListener(v -> startActivity(new Intent(this, message_history.class)));

        LinearLayout buttonBlocked = findViewById(R.id.blocked_home);
        buttonBlocked.setOnClickListener(v -> startActivity(new Intent(this, blocked_numbers.class)));

        LinearLayout buttonSpam = findViewById(R.id.spam_home);
        buttonSpam.setOnClickListener(v -> startActivity(new Intent(this, spam_messages.class)));

        LinearLayout buttonSettings = findViewById(R.id.setting_home);
        buttonSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, profile.class));
                return true;
            }
            return false;
        });
    }

    private void addDataToFirebase(String phoneNumber, String message) {
        if (user == null) {
            Toast.makeText(this, "User not logged in. Cannot save data.", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        message_F message_f = new message_F();
        message_f.setEmail(user.getEmail());
        message_f.setPhoneNumber(phoneNumber);
        message_f.setMessage(message);
        message_f.setDateTime(currentDateTime);

        databaseReference.push().setValue(message_f)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Data added successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void checkUserPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_CALL_LOG
            }, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    private void loadBlockedCalls() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "READ_CALL_LOG permission not granted");
            Toast.makeText(this, "Call log permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] projection = new String[]{
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.TYPE
        };
        String selection = CallLog.Calls.TYPE + "=?";
        String[] selectionArgs = new String[]{String.valueOf(CallLog.Calls.BLOCKED_TYPE)};

        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                CallLog.Calls.DATE + " DESC"
        );

        spamCallsList.clear();
        if (cursor != null) {
            int count = 0;
            while (cursor.moveToNext() && count < 10) { // Limit to 10 entries for performance
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                long date = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(date));
                spamCallsList.add(number + " - " + formattedDate);
                count++;
                Log.d(TAG, "Added blocked call: " + number + " - " + formattedDate);
            }
            cursor.close();
            spamCallsAdapter.notifyDataSetChanged();
            if (spamCallsList.isEmpty()) {
                Log.w(TAG, "No blocked calls found in call log");
                spamCallsList.add("No blocked calls found");
                spamCallsAdapter.notifyDataSetChanged();
            }
        } else {
            Log.e(TAG, "Cursor is null when querying call log");
            spamCallsList.add("Error loading blocked calls");
            spamCallsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS && grantResults.length > 0) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
                loadBlockedCalls();
            } else {
                Toast.makeText(this, "Permissions denied. Cannot load blocked calls.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}