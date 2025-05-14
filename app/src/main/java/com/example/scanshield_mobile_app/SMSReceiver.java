package com.example.scanshield_mobile_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private static final String CHANNEL_ID = "IncomingMessageChannel";
    private static final int NOTIFICATION_ID = 1001;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || !intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            return;
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                .getReference("messageData");

        if (user == null) {
            Log.e(TAG, "User not logged in. Cannot process SMS.");
            return;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Log.e(TAG, "No SMS data in intent extras");
            return;
        }

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) {
            Log.e(TAG, "No PDU data in SMS bundle");
            return;
        }

        for (Object pdu : pdus) {
            SmsMessage smsMessage;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdu, bundle.getString("format"));
            } else {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            }

            String sender = smsMessage.getOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();
            long timestamp = smsMessage.getTimestampMillis();
            String dateTime = String.valueOf(timestamp);

            Log.d(TAG, "Received SMS from: " + sender + ", Message: " + messageBody);

            // Upload to Firebase
            uploadMessageToFirebase(context, sender, messageBody, dateTime, timestamp);

            // Show popup dialog
            showPopupDialog(context, sender, messageBody, timestamp);

            // Show notification
            showNotification(context, sender, messageBody, timestamp);
        }
    }

    private void uploadMessageToFirebase(Context context, String phoneNumber, String message, String dateTime, long timestamp) {
        if (user == null) {
            Log.e(TAG, "User not logged in. Cannot upload message.");
            return;
        }

        String key = databaseReference.push().getKey();
        message_F messageObj = new message_F();
        messageObj.setEmail(user.getEmail());
        messageObj.setPhoneNumber(phoneNumber);
        messageObj.setMessage(message);
        messageObj.setDateTime(dateTime);
        messageObj.setRead(false); // Explicitly set to false
        messageObj.setIsSpam(false); // Default to false, will update after detection

        databaseReference.child(key).setValue(messageObj, (error, ref) -> {
            if (error == null) {
                // Check spam and update status
                boolean isSpam = SpamDetector.isSpam(message);
                databaseReference.child(key).child("isSpam").setValue(isSpam);
                Log.d(TAG, "Message uploaded and spam status set: " + isSpam);
            } else {
                Log.e(TAG, "Failed to upload message: " + error.getMessage());
            }
        });
    }

    private void showPopupDialog(Context context, String sender, String messageBody, long timestamp) {
        Intent dialogIntent = new Intent(context, MessagePopupActivity.class);
        dialogIntent.putExtra("sender", sender);
        dialogIntent.putExtra("message", messageBody);
        dialogIntent.putExtra("timestamp", timestamp);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(dialogIntent);
    }

    private void showNotification(Context context, String sender, String messageBody, long timestamp) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Incoming Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for incoming messages");
            notificationManager.createNotificationChannel(channel);
        }

        // Intent for "View Message"
        Intent viewIntent = new Intent(context, message_history.class);
        viewIntent.putExtra("sender", sender);
        viewIntent.putExtra("message", messageBody);
        viewIntent.putExtra("timestamp", timestamp);
        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(
                context,
                0,
                viewIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_message_notification)
                .setContentTitle("New Message from " + sender)
                .setContentText(messageBody.length() > 30 ? messageBody.substring(0, 30) + "..." : messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(viewPendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}