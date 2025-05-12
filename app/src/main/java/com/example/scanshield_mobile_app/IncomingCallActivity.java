package com.example.scanshield_mobile_app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IncomingCallActivity extends Activity {

    private TextView numberView;
    private TextView statusView;
    private Button answerButton;
    private Button rejectButton;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming_call_activity);

        // Show over lock screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        numberView = findViewById(R.id.caller_number);
        statusView = findViewById(R.id.caller_status);
        answerButton = findViewById(R.id.button_answer);
        rejectButton = findViewById(R.id.button_reject);

        String number = getIntent().getStringExtra("caller_number");
        String status = getIntent().getStringExtra("caller_status");

        if (number == null || number.isEmpty()) {
            numberView.setText("Unknown Number");
            statusView.setText("Status: unknown");
            finish();
            return;
        }

        numberView.setText(number);
        statusView.setText("Status: " + (status != null ? status : "unknown"));

        // Answer button
        answerButton.setOnClickListener(v -> {
            TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ANSWER_PHONE_CALLS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                telecomManager.acceptRingingCall();
                finish();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ANSWER_PHONE_CALLS}, 1);
            }
        });

        // Reject button
        rejectButton.setOnClickListener(v -> {
            TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ANSWER_PHONE_CALLS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                telecomManager.endCall();
                finish();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ANSWER_PHONE_CALLS}, 1);
            }
        });

        // Listen for call state to dismiss UI
        setupPhoneStateListener();
    }

    private void setupPhoneStateListener() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                if (state == TelephonyManager.CALL_STATE_IDLE || state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    finish();
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (telephonyManager != null && phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }
}