package com.example.scanshield_mobile_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Reciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
                 Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu, bundle.getString("format"));
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();

                        // 🔥 Add FirebaseAuth here
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        String email = "Unknown User"; // default if user not logged
                        if (currentUser != null) {
                            email = currentUser.getEmail();
                        }

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("messageData");

                        message_F message_f = new message_F();
                        message_f.setEmail(email);
                        message_f.setPhoneNumber(sender);
                        message_f.setMessage(messageBody);
                        message_f.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

                        ref.push().setValue(message_f);
                    }
                }
            }
        }
    }


