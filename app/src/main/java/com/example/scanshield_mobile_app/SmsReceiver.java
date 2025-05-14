package com.example.scanshield_mobile_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = smsMessage.getOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();
                        String dateTime = String.valueOf(smsMessage.getTimestampMillis());

                        // Upload to Firebase
                        uploadMessageToFirebase(sender, messageBody, dateTime);
                    }
                }
            }
        }
    }

    private void uploadMessageToFirebase(String phoneNumber, String message, String dateTime) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                    .getReference("messageData");
            String key = databaseReference.push().getKey();
            message_F messageObj = new message_F();
            messageObj.setEmail(user.getEmail());
            messageObj.setPhoneNumber(phoneNumber);
            messageObj.setMessage(message);
            messageObj.setDateTime(dateTime);
            // Initially set spam status as null, will update after detection
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
    }
}