package com.example.scanshield_mobile_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneCallReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneCallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        Log.d(TAG, "onReceive: Phone state changed - state=" + state + ", number=" + number);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state) && number != null) {
            Log.d(TAG, "onReceive: Incoming call detected, launching IncomingCallActivity for number=" + number);
            Intent activityIntent = new Intent(context, IncomingCallActivity.class);
            activityIntent.putExtra("incoming_number", number);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            activityIntent.setPackage(context.getPackageName());
            activityIntent.putExtra("timestamp", java.time.LocalTime.now().toString());
            try {
                context.startActivity(activityIntent);
                Log.d(TAG, "onReceive: Successfully launched IncomingCallActivity for " + number);
            } catch (Exception e) {
                Log.e(TAG, "onReceive: Failed to launch IncomingCallActivity: " + e.getMessage(), e);
            }
        }
    }
}