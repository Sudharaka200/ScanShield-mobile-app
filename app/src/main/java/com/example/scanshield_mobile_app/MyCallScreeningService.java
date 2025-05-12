package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.net.Uri;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyCallScreeningService extends CallScreeningService {
    private static final String TAG = "CallScreeningService";

    @Override
    public void onScreenCall(Call.Details callDetails) {
        Uri handle = callDetails.getHandle();
        if (handle == null) {
            respondWithDefault(callDetails);
            return;
        }

        String incomingNumber = handle.getSchemeSpecificPart();
        String normalizedNumber = normalizePhoneNumber(incomingNumber);
        Log.d(TAG, "Incoming number: " + incomingNumber + ", normalized: " + normalizedNumber);

        if (normalizedNumber == null) {
            respondWithDefault(callDetails);
            return;
        }

        DatabaseReference database = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                .getReference("SpamNumbers");
        database.child(normalizedNumber).child("status").get().addOnCompleteListener(task -> {
            CallResponse.Builder responseBuilder = new CallResponse.Builder()
                    .setDisallowCall(false)
                    .setRejectCall(false)
                    .setSkipCallLog(false)
                    .setSkipNotification(false);

            String status = "unknown";
            if (task.isSuccessful() && task.getResult() != null) {
                status = task.getResult().getValue(String.class);
                if ("spam".equalsIgnoreCase(status)) {
                    responseBuilder.setRejectCall(true);
                    responseBuilder.setDisallowCall(true);
                    Log.d(TAG, "Call blocked as spam: " + normalizedNumber);
                } else {
                    Log.d(TAG, "Call marked as not spam: " + normalizedNumber);
                }
            } else {
                Log.e(TAG, "Failed to read status for " + normalizedNumber + ": " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }

            respondToCall(callDetails, responseBuilder.build());

            // Notify IncomingCallActivity
            Intent intent = new Intent(this, IncomingCallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("caller_number", incomingNumber);
            intent.putExtra("caller_status", status);
            startActivity(intent);
        });
    }

    private void respondWithDefault(Call.Details callDetails) {
        CallResponse response = new CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build();
        respondToCall(callDetails, response);
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