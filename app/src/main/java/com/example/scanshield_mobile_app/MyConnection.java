package com.example.scanshield_mobile_app;

import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.util.Log;

public class MyConnection extends Connection {

    @Override
    public void onAnswer() {
        super.onAnswer();
        Log.d("MyConnection", "Call answered");
        setActive();
    }

    @Override
    public void onReject() {
        super.onReject();
        Log.d("MyConnection", "Call rejected");
        setDisconnected(new DisconnectCause(DisconnectCause.REJECTED));
        destroy();
    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();
        Log.d("MyConnection", "Call disconnected");
        setDisconnected(new DisconnectCause(DisconnectCause.LOCAL));
        destroy();
    }

    @Override
    public void onStateChanged(int state) {
        super.onStateChanged(state);
        Log.d("MyConnection", "Connection state changed: " + stateToString(state));
        // Notify IncomingCallActivity to dismiss if call ends
        if (state == STATE_DISCONNECTED || state == STATE_HOLDING) {
            // Broadcast or use other mechanism to dismiss UI
        }
    }
}