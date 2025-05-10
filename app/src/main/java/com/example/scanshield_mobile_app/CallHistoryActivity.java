package com.example.scanshield_mobile_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;

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
    private ArrayList<CallModel> callList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        callList = new ArrayList<>();
        callAdapter = new CallAdapter(callList);
        recyclerView.setAdapter(callAdapter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSION_REQUEST_CODE);
        } else {
            loadCallLogs();
        }
    }

    private void loadCallLogs() {
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

        if (cursor != null) {
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                String number = cursor.getString(numberIndex);
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

                String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(new Date(Long.parseLong(cursor.getString(dateIndex))));
                String duration = cursor.getString(durationIndex);

                boolean isSpam = number != null && number.endsWith("9999"); // Fake spam rule

                callList.add(new CallModel(number, type, dateStr, duration, isSpam));
            }
            cursor.close();
            callAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadCallLogs();
        } else {
            // Handle permission denial
        }
    }
}
