package com.example.scanshield_mobile_app;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StatusUtils {

    public static void updateCallerStatus(String number, String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("caller_status")
                .child(number);
        reference.setValue(new CallerStatus(number, status));
    }

    public interface StatusCallback {
        void onStatusReceived(@Nullable String status);
    }

    public static void getCallerStatus(String number, StatusCallback callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("caller_status")
                .child(number);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    CallerStatus callerStatus = snapshot.getValue(CallerStatus.class);
                    callback.onStatusReceived(callerStatus.getStatus());
                } else {
                    callback.onStatusReceived(null);
                }
            } else {
                callback.onStatusReceived(null);
            }
        });
    }
}
