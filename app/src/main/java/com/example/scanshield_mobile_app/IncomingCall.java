package com.example.scanshield_mobile_app;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class IncomingCall extends Activity {

    private TextView numberTextView, statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming_call_activity);

        numberTextView = findViewById(R.id.caller_number);
        statusTextView = findViewById(R.id.caller_status);


        String incomingNumber = getIntent().getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        numberTextView.setText("Number: " + incomingNumber);

        if (incomingNumber != null) {
            StatusUtils.getCallerStatus(incomingNumber, status -> {
                if (status != null) {
                    statusTextView.setText("Status: " + status);
                } else {
                    statusTextView.setText("Status: Unknown");
                }
            });
        }
    }
}
