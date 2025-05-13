package com.example.scanshield_mobile_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CallHistoryActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private CallAdapter callAdapter;
    private ArrayList<CallLogModel> callList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        callList = new ArrayList<>();
        callAdapter = new CallAdapter(this, callList);
        recyclerView.setAdapter(callAdapter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
        } else {
            loadCallLogs();
        }
    }

    private void loadCallLogs() {
        callList.clear();
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

        if (cursor != null) {
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                String number = cursor.getString(numberIndex);
                String normalizedNumber = normalizePhoneNumber(number);
                String type;
                switch (cursor.getInt(typeIndex)) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        type = "Outgoing";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        type = "Incoming";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        type = "Missed";
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        type = "Rejected";
                        break;
                    default:
                        type = "Other";
                        break;
                }

                long dateLong = Long.parseLong(cursor.getString(dateIndex));
                String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(dateLong));
                String timeStr = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(dateLong));
                String duration = cursor.getString(durationIndex);
                String name = getContactName(normalizedNumber);

                callList.add(new CallLogModel(normalizedNumber, name, type, dateStr, timeStr, duration));
            }
            cursor.close();
            callAdapter.notifyDataSetChanged();
        }
    }

    private String getContactName(String phoneNumber) {
        if (phoneNumber == null) return null;
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{phoneNumber},
                null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            cursor.close();
            return name;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    private String normalizePhoneNumber(String number) {
        if (number == null || number.isEmpty()) return null;
        String normalized = number.replaceAll("[^0-9]", "");
        if (normalized.startsWith("1") && normalized.length() > 10) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadCallLogs();
        } else {
            // Handle permission denial
        }
    }
}