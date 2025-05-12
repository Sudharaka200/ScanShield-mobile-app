package com.example.scanshield_mobile_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) return;

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state) && number != null) {
                Log.d("Receiver", "Incoming call from: " + number);
                // Optionally trigger additional logic, but MyCallScreeningService handles the UI
            }
        } else if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                    String sender = sms.getOriginatingAddress();
                    String message = sms.getMessageBody();
                    Log.d("Receiver", "SMS from: " + sender + ", message: " + message);
                    // Handle SMS spam detection if needed
                }
            }
        }
    }
}