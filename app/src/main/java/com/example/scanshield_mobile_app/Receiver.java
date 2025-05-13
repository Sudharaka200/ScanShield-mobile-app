package com.example.scanshield_mobile_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Receiver extends BroadcastReceiver {
    private static final String TAG = "Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) return;

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state) && incomingNumber != null) {
                String normalizedNumber = normalizePhoneNumber(incomingNumber);
                Log.d(TAG, "Incoming call from: " + incomingNumber + ", normalized: " + normalizedNumber);

                if (normalizedNumber != null) {
                    // Query Firebase for spam status
                    DatabaseReference database = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                            .getReference("SpamNumbers");
                    database.child(normalizedNumber).child("status").get().addOnCompleteListener(task -> {
                        String status = "unknown";
                        if (task.isSuccessful() && task.getResult() != null) {
                            status = task.getResult().getValue(String.class);
                            Log.d(TAG, "Spam status for " + normalizedNumber + ": " + status);
                        } else {
                            Log.e(TAG, "Failed to read spam status: " +
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                        }

                        // Launch popup
                        Intent popupIntent = new Intent(context, IncomingCallActivity.class);
                        popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        popupIntent.putExtra("caller_number", incomingNumber);
                        popupIntent.putExtra("spam_type", status != null ? status : "unknown");
                        context.startActivity(popupIntent);
                    });
                }
            }
        }
    }

    private String normalizePhoneNumber(String number) {
        if (number == null || number.isEmpty()) return null;
        String normalized = number.replaceAll("[^0-9]", "");
        if (normalized.startsWith("1") && normalized.length() > 10) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }
}