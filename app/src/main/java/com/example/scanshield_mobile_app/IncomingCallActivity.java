package com.example.scanshield_mobile_app;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressWarnings("deprecation")
public class IncomingCallActivity extends AppCompatActivity {

    private TextView callerNumberTextView;
    private TextView callerStatusTextView;
    private ImageView callerIconImageView;
    private Button answerButton;
    private Button rejectButton;
    private Button markAsSpamButton;
    private DatabaseReference databaseReference;
    private String normalizedNumber;
    private TelecomManager telecomManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        // Set window flags to ensure full display over lock screen and other apps
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); // Ensures full screen overlay

        // Initialize TelecomManager
        telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);

        // Initialize UI components
        callerNumberTextView = findViewById(R.id.caller_number);
        callerStatusTextView = findViewById(R.id.caller_status);
        callerIconImageView = findViewById(R.id.caller_icon);
        answerButton = findViewById(R.id.button_answer);
        rejectButton = findViewById(R.id.button_reject);
        markAsSpamButton = findViewById(R.id.button_mark_as_spam);

        // Get the incoming call number from the intent
        Intent intent = getIntent();
        String incomingNumber = intent.getStringExtra("incoming_number");
        if (incomingNumber != null) {
            callerNumberTextView.setText(incomingNumber);
            normalizedNumber = normalizePhoneNumber(incomingNumber);
            Log.d("IncomingCallActivity", "Incoming call from: " + incomingNumber + ", normalized: " + normalizedNumber);
            checkNumberInFirebase(normalizedNumber);
        } else {
            callerNumberTextView.setText("Unknown Number");
            callerStatusTextView.setText("Status: unknown");
        }

        // Set up button listeners
        answerButton.setOnClickListener(v -> {
            Log.d("IncomingCallActivity", "Answer button clicked");
            answerCall();
        });

        rejectButton.setOnClickListener(v -> {
            Log.d("IncomingCallActivity", "Reject button clicked");
            rejectCall();
        });

        markAsSpamButton.setOnClickListener(v -> {
            Log.d("IncomingCallActivity", "Mark as spam button clicked");
            markAsSpam();
        });
    }

    private String normalizePhoneNumber(String phoneNumber) {
        String normalized = phoneNumber.replaceAll("[^0-9]", "");
        normalized = normalized.substring(Math.max(0, normalized.length() - 10));
        Log.d("IncomingCallActivity", "Final normalized number: " + normalized);
        return normalized;
    }

    private void checkNumberInFirebase(String number) {
        Log.d("IncomingCallActivity", "Checking Firebase for number: " + number);
        databaseReference = FirebaseDatabase.getInstance().getReference("SpamNumbers").child(number).child("status");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                if (status != null) {
                    callerStatusTextView.setText("Status: " + status);
                    Log.d("IncomingCallActivity", "Spam status for " + number + ": " + status);
                } else {
                    callerStatusTextView.setText("Status: not found");
                    Log.d("IncomingCallActivity", "No spam status found for " + number);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callerStatusTextView.setText("Status: error");
                Log.e("IncomingCallActivity", "Firebase error: " + databaseError.getMessage());
            }
        });
    }

    private void answerCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                telecomManager.acceptRingingCall();
                Log.d("IncomingCallActivity", "Call answered successfully");
                Toast.makeText(this, "Call answered", Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                Log.e("IncomingCallActivity", "Permission ANSWER_PHONE_CALLS not granted", e);
                Toast.makeText(this, "Permission to answer calls not granted", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.w("IncomingCallActivity", "Answering calls not supported on this API level");
            Toast.makeText(this, "Answering calls not supported on this device", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private void rejectCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                telecomManager.endCall();
                Log.d("IncomingCallActivity", "Call rejected successfully");
                Toast.makeText(this, "Call rejected", Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                Log.e("IncomingCallActivity", "Permission to end calls not granted", e);
                Toast.makeText(this, "Permission to reject calls not granted", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.w("IncomingCallActivity", "Rejecting calls not supported on this API level");
            Toast.makeText(this, "Rejecting calls not supported on this device", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private void markAsSpam() {
        if (normalizedNumber != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("SpamNumbers").child(normalizedNumber);
            databaseReference.child("status").setValue("spam", (error, ref) -> {
                if (error == null) {
                    Toast.makeText(IncomingCallActivity.this, "Number marked as spam", Toast.LENGTH_SHORT).show();
                    Log.d("IncomingCallActivity", "Marked " + normalizedNumber + " as spam in Firebase");
                } else {
                    Toast.makeText(IncomingCallActivity.this, "Failed to mark as spam", Toast.LENGTH_SHORT).show();
                    Log.e("IncomingCallActivity", "Firebase error marking as spam: " + error.getMessage());
                }
            });
        }
        finish();
    }
}