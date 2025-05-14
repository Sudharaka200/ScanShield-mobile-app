package com.example.scanshield_mobile_app;

import static android.telecom.TelecomManager.PRESENTATION_ALLOWED;

import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.util.Log;

public class MyConnectionService extends ConnectionService {

    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Log.d("MyConnectionService", "Incoming call");
        MyConnection connection = new MyConnection();
        connection.setAddress(request.getAddress(), PRESENTATION_ALLOWED);
        connection.setRinging();
        return connection;
    }

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Log.d("MyConnectionService", "Outgoing call");
        MyConnection connection = new MyConnection();
        connection.setAddress(request.getAddress(), PRESENTATION_ALLOWED);
        connection.setDialing();
        return connection;
    }
}