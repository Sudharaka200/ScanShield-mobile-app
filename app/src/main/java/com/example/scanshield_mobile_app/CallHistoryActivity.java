package com.example.scanshield_mobile_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CallHistoryActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private CallAdapter callAdapter;
    private ArrayList<CallLogModel> callList;
    private FirebaseAuth mAuth;
    private TextView lUser;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                .getReference("callData");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        callList = new ArrayList<>();
        callAdapter = new CallAdapter(this, callList);
        recyclerView.setAdapter(callAdapter);

        if (user == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
        } else {
            loadCallLogs();
        }

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
                    startActivity(new Intent(CallHistoryActivity.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(CallHistoryActivity.this, SettingsActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(CallHistoryActivity.this, profile.class));
                    return true;
                }
                return false;
            }
        });

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

    private void loadCallLogs() {
        callList.clear();
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

        if (cursor != null) {
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                String number = cursor.getString(numberIndex);
                String normalizedNumber = normalizePhoneNumber(number);
                String type;
                boolean isSpam = false;
                switch (cursor.getInt(typeIndex)) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        type = "Outgoing";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        type = "Incoming";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        type = "Missed";
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        type = "Rejected";
                        isSpam = true; // Mark rejected calls as spam
                        break;
                    default:
                        type = "Other";
                        break;
                }

                long dateLong = Long.parseLong(cursor.getString(dateIndex));
                String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(dateLong));
                String timeStr = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(dateLong));
                String dateTime = dateStr + " " + timeStr;
                String duration = cursor.getString(durationIndex);
                String name = getContactName(normalizedNumber);

                callList.add(new CallLogModel(normalizedNumber, name, type, dateStr, timeStr, duration));

                // If the call is marked as spam, upload to Firebase
                if (isSpam) {
                    uploadToFirebase(normalizedNumber, type, dateTime);
                }
            }
            cursor.close();
            callAdapter.notifyDataSetChanged();
        }
    }

    private void uploadToFirebase(String phoneNumber, String callStatus, String dateTime) {
        call_F call = new call_F();
        call.setEmail(user.getEmail());
        call.setPhoneNumber(phoneNumber);
        call.setCallStatus(callStatus);
        call.setDateTime(dateTime);
        call.setIsSpam(true);

        databaseReference.push().setValue(call)
                .addOnSuccessListener(aVoid -> {
                    // Successfully uploaded
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private String getContactName(String phoneNumber) {
        if (phoneNumber == null) return null;
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{phoneNumber},
                null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            cursor.close();
            return name;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    private String normalizePhoneNumber(String number) {
        if (number == null || number.isEmpty()) return null;
        String normalized = number.replaceAll("[^0-9]", "");
        if (normalized.startsWith("1") && normalized.length() > 10) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadCallLogs();
        } else {
            // Handle permission denial
        }
    }
}