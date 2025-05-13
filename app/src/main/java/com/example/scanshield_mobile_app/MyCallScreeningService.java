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

    private static final String CHANNEL_ID = "SpamCallChannel";
    private DatabaseReference databaseReference;

    @Override
    public void onScreenCall(Call.Details callDetails) {
        String phoneNumber = callDetails.getHandle() != null ? callDetails.getHandle().getSchemeSpecificPart() : null;
        if (phoneNumber == null) {
            Log.e("MyCallScreeningService", "Phone number is null");
            respondToCall(callDetails, new CallResponse.Builder().build());
            return;
        }

        String normalizedNumber = normalizePhoneNumber(phoneNumber);
        Log.d("MyCallScreeningService", "Screening call from: " + phoneNumber + ", normalized: " + normalizedNumber);

        databaseReference = FirebaseDatabase.getInstance().getReference("SpamNumbers").child(normalizedNumber).child("status");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                CallResponse response;
                if ("spam".equals(status)) {
                    Log.d("MyCallScreeningService", "Blocking spam call: " + normalizedNumber);
                    response = new CallResponse.Builder()
                            .setDisallowCall(true)
                            .setRejectCall(true)
                            .setSkipCallLog(false)
                            .setSkipNotification(true)
                            .build();
                    showSpamNotification(phoneNumber); // Notify user
                } else {
                    Log.d("MyCallScreeningService", "Allowing call: " + normalizedNumber);
                    Intent intent = new Intent(MyCallScreeningService.this, IncomingCallActivity.class);
                    intent.putExtra("incoming_number", phoneNumber);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    response = new CallResponse.Builder().build();
                }
                respondToCall(callDetails, response);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MyCallScreeningService", "Firebase error: " + databaseError.getMessage());
                respondToCall(callDetails, new CallResponse.Builder().build());
            }
        });
    }

    private String normalizePhoneNumber(String phoneNumber) {
        String normalized = phoneNumber.replaceAll("[^0-9]", "");
        normalized = normalized.substring(Math.max(0, normalized.length() - 10));
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
    }
}