package com.example.scanshield_mobile_app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressWarnings("deprecation")
public class IncomingCallActivity extends AppCompatActivity {

    private static final String TAG = "IncomingCallActivity";

    private TextView callerNumberTextView;
    private TextView callerStatusTextView;
    private ImageView callerIconImageView;
    private Button answerButton;
    private Button rejectButton;
    private DatabaseReference databaseReference;
    private String normalizedNumber;
    private TelecomManager telecomManager;
    private ValueEventListener statusListener;
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Activity started at " + java.time.LocalTime.now());
        setContentView(R.layout.activity_incoming_call);

        // Set window flags
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Initialize TelecomManager
        telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);

        // Initialize UI components
        callerNumberTextView = findViewById(R.id.caller_number);
        callerStatusTextView = findViewById(R.id.caller_status);
        callerIconImageView = findViewById(R.id.caller_icon);
        answerButton = findViewById(R.id.button_answer);
        rejectButton = findViewById(R.id.button_reject);

        if (callerStatusTextView == null || callerNumberTextView == null) {
            Log.e(TAG, "onCreate: UI component initialization failed");
            Toast.makeText(this, "UI initialization error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize Handler for UI updates
        uiHandler = new Handler(Looper.getMainLooper());

        // Get incoming number from intent
        String incomingNumber = getIncomingNumber();
        if (incomingNumber == null || incomingNumber.isEmpty()) {
            Log.e(TAG, "onCreate: No valid number found");
            callerNumberTextView.setText("Unknown Number");
            callerStatusTextView.setText("Status: error");
            setCallerIcon("unknown");
            return;
        }

        callerNumberTextView.setText(incomingNumber);
        normalizedNumber = normalizePhoneNumber(incomingNumber);
        Log.d(TAG, "onCreate: Raw number: " + incomingNumber + ", Normalized: " + normalizedNumber);
        checkNumberInFirebase(normalizedNumber);

        // Set up button listeners
        answerButton.setOnClickListener(v -> answerCall());
        rejectButton.setOnClickListener(v -> rejectCall());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: Received new intent at " + java.time.LocalTime.now());
        setIntent(intent);
        String incomingNumber = getIncomingNumber();
        if (incomingNumber != null && !incomingNumber.isEmpty()) {
            callerNumberTextView.setText(incomingNumber);
            normalizedNumber = normalizePhoneNumber(incomingNumber);
            Log.d(TAG, "onNewIntent: Updated number: " + incomingNumber + ", Normalized: " + normalizedNumber);
            checkNumberInFirebase(normalizedNumber);
        } else {
            Log.e(TAG, "onNewIntent: No valid number in new intent");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseReference != null && statusListener != null) {
            databaseReference.removeEventListener(statusListener);
            Log.d(TAG, "onDestroy: Removed Firebase listener for " + normalizedNumber);
        }
        Log.d(TAG, "onDestroy: Activity destroyed at " + java.time.LocalTime.now());
    }

    private String getIncomingNumber() {
        Intent intent = getIntent();
        String incomingNumber = null;
        if (intent != null && intent.getExtras() != null) {
            incomingNumber = intent.getStringExtra("incoming_number");
            String timestamp = intent.getStringExtra("timestamp");
            Log.d(TAG, "getIncomingNumber: Intent extras - incoming_number=" + incomingNumber + ", timestamp=" + timestamp);
        } else {
            Log.e(TAG, "getIncomingNumber: Intent or extras are null");
        }

        // Fallback to TelephonyManager
        if (incomingNumber == null) {
            Log.w(TAG, "getIncomingNumber: No incoming number in intent, attempting fallback");
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    incomingNumber = telephonyManager.getLine1Number();
                    Log.d(TAG, "getIncomingNumber: Fallback number from TelephonyManager: " + incomingNumber);
                } else {
                    Log.w(TAG, "getIncomingNumber: READ_PHONE_STATE permission not granted");
                }
            } catch (Exception e) {
                Log.e(TAG, "getIncomingNumber: TelephonyManager error: " + e.getMessage());
            }
        }
        return incomingNumber;
    }

    private String normalizePhoneNumber(String phoneNumber) {
        String normalized = phoneNumber.replaceAll("[^0-9]", "");
        normalized = normalized.substring(Math.max(0, normalized.length() - 10));
        Log.d(TAG, "normalizePhoneNumber: Normalized number: " + normalized);
        return normalized;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void checkNumberInFirebase(String number) {
        Log.d(TAG, "checkNumberInFirebase: Querying Firebase for number: " + number);
        if (!isNetworkAvailable()) {
            Log.w(TAG, "checkNumberInFirebase: No network available");
            callerStatusTextView.setText("Status: no network");
            setCallerIcon("unknown");
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("SpamNumbers").child(number).child("status");
        statusListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: DataSnapshot exists=" + dataSnapshot.exists() + ", key=" + dataSnapshot.getKey() + ", value=" + dataSnapshot.getValue());
                uiHandler.post(() -> {
                    String status = dataSnapshot.getValue(String.class);
                    if (status != null) {
                        callerStatusTextView.setText("Status: " + status);
                        setCallerIcon(status);
                        Log.d(TAG, "checkNumberInFirebase: Spam status for " + number + ": " + status);
                    } else {
                        callerStatusTextView.setText("Status: not found");
                        setCallerIcon("unknown");
                        Log.d(TAG, "checkNumberInFirebase: No spam status found for " + number);
                    }
                    callerStatusTextView.invalidate();
                    callerStatusTextView.getParent().requestLayout();
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Firebase error: " + databaseError.getMessage() + ", code: " + databaseError.getCode());
                uiHandler.post(() -> {
                    callerStatusTextView.setText("Status: error");
                    setCallerIcon("unknown");
                });
            }
        };
        databaseReference.addValueEventListener(statusListener);
    }

    private void setCallerIcon(String status) {
        int iconRes = "spam".equalsIgnoreCase(status) ? R.drawable.ic_spam :
                "not_spam".equalsIgnoreCase(status) ? R.drawable.ic_safe :
                        R.drawable.ic_unknown;
        callerIconImageView.setImageResource(iconRes);
    }

    private void answerCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                telecomManager.acceptRingingCall();
                Log.d(TAG, "answerCall: Call answered successfully");
                Toast.makeText(this, "Call answered", Toast.LENGTH_SHORT).show();
                finish(); // Ensure activity closes after answering
            } catch (SecurityException e) {
                Log.e(TAG, "answerCall: Permission error: " + e.getMessage());
                Toast.makeText(this, "Permission to answer calls not granted", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.w(TAG, "answerCall: Answering calls not supported on this API level");
            Toast.makeText(this, "Answering calls not supported on this device", Toast.LENGTH_LONG).show();
        }
        finish(); // Force finish to prevent default UI
    }

    private void rejectCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                telecomManager.endCall();
                Log.d(TAG, "rejectCall: Call rejected successfully");
                Toast.makeText(this, "Call rejected", Toast.LENGTH_SHORT).show();
                finish(); // Ensure activity closes after rejecting
            } catch (SecurityException e) {
                Log.e(TAG, "rejectCall: Permission error: " + e.getMessage());
                Toast.makeText(this, "Permission to reject calls not granted", Toast.LENGTH_LONG).show();
            }
        }
        finish(); // Force finish to prevent default UI
    }
}