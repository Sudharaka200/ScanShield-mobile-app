package com.example.scanshield_mobile_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyCallScreeningService extends CallScreeningService {

    private static final String TAG = "MyCallScreeningService";
    private static final String CHANNEL_ID = "SpamCallChannel";
    private DatabaseReference databaseReference;

    @Override
    public void onScreenCall(Call.Details callDetails) {
        Log.d(TAG, "onScreenCall: Started at " + java.time.LocalTime.now());
        String phoneNumber = callDetails.getHandle() != null ? callDetails.getHandle().getSchemeSpecificPart() : null;
        if (phoneNumber == null) {
            Log.e(TAG, "onScreenCall: Phone number is null");
            respondToCall(callDetails, new CallResponse.Builder().setDisallowCall(true).build());
            return;
        }

        String normalizedNumber = normalizePhoneNumber(phoneNumber);
        Log.d(TAG, "onScreenCall: Screening call from: " + phoneNumber + ", normalized: " + normalizedNumber);

        databaseReference = FirebaseDatabase.getInstance().getReference("SpamNumbers").child(normalizedNumber).child("status");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Firebase query completed for " + normalizedNumber + " at " + java.time.LocalTime.now());
                Log.d(TAG, "onDataChange: DataSnapshot exists=" + dataSnapshot.exists() + ", key=" + dataSnapshot.getKey() + ", value=" + dataSnapshot.getValue());
                String status = dataSnapshot.getValue(String.class);
                CallResponse response;
                if ("spam".equalsIgnoreCase(status)) {
                    Log.d(TAG, "onDataChange: Blocking spam call: " + normalizedNumber);
                    response = new CallResponse.Builder()
                            .setDisallowCall(true)
                            .setRejectCall(true)
                            .setSkipCallLog(false)
                            .setSkipNotification(true)
                            .build();
                    showSpamNotification(phoneNumber);
                } else {
                    Log.d(TAG, "onDataChange: Allowing call: " + normalizedNumber + ", status=" + (status != null ? status : "not found"));
                    Intent intent = new Intent(MyCallScreeningService.this, IncomingCallActivity.class);
                    intent.putExtra("incoming_number", phoneNumber);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    intent.setPackage(getPackageName());
                    intent.putExtra("timestamp", java.time.LocalTime.now().toString());
                    Log.d(TAG, "onDataChange: Launching IncomingCallActivity with extras - incoming_number=" + phoneNumber + ", timestamp=" + intent.getStringExtra("timestamp"));
                    try {
                        startActivity(intent);
                        Log.d(TAG, "onDataChange: Successfully launched IncomingCallActivity for " + phoneNumber + " at " + java.time.LocalTime.now());
                    } catch (Exception e) {
                        Log.e(TAG, "onDataChange: Failed to launch IncomingCallActivity: " + e.getMessage(), e);
                    }
                    // Explicitly allow the call and suppress default UI
                    response = new CallResponse.Builder()
                            .setDisallowCall(false)
                            .setSkipCallLog(false) // Allow call log
                            .setSkipNotification(true) // Suppress default notification
                            .build();
                }
                respondToCall(callDetails, response);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Firebase error: " + databaseError.getMessage() + ", code: " + databaseError.getCode());
                respondToCall(callDetails, new CallResponse.Builder().setDisallowCall(true).build());
            }
        });
    }

    private String normalizePhoneNumber(String phoneNumber) {
        String normalized = phoneNumber.replaceAll("[^0-9]", "");
        normalized = normalized.substring(Math.max(0, normalized.length() - 10));
        Log.d(TAG, "normalizePhoneNumber: Input: " + phoneNumber + ", Normalized: " + normalized);
        return normalized;
    }

    private void showSpamNotification(String phoneNumber) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Spam Call Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Blocked Spam Call")
                .setContentText("Blocked a spam call from " + phoneNumber)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify((int) System.currentTimeMillis(), notification);
        Log.d(TAG, "showSpamNotification: Notification shown for " + phoneNumber);
    }
}