//package com.example.scanshield_mobile_app;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.provider.CallLog;
//import android.view.MenuItem;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.navigation.NavigationBarView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class call_history extends AppCompatActivity {
//
//    private static final int PERMISSIONS_REQUEST_READ_CALL_LOG = 100;
//    FirebaseAuth mAuth;
//    TextView logedUser;
//    FirebaseUser user;
//    RecyclerView recyclerView;
//    List<CallModel> callList;
//    CallAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_call_history);
//
//        mAuth = FirebaseAuth.getInstance();
//        logedUser = findViewById(R.id.loginUserEmailCallHistory);
//        user = mAuth.getCurrentUser();
//        if (user != null) {
//            logedUser.setText(user.getEmail());
//        }
//
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        callList = new ArrayList<>();
//        adapter = new CallAdapter(this, callList);
//        recyclerView.setAdapter(adapter);
//
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.nav_home) {
//                startActivity(new Intent(call_history.this, Home.class));
//                return true;
//            } else if (item.getItemId() == R.id.nav_settings) {
//                startActivity(new Intent(call_history.this, SettingsActivity.class));
//                return true;
//            } else if (item.getItemId() == R.id.nav_profile) {
//                startActivity(new Intent(call_history.this, profile.class));
//                return true;
//            }
//            return false;
//        });
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_READ_CALL_LOG);
//        } else {
//            fetchCallLogs();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSIONS_REQUEST_READ_CALL_LOG && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            fetchCallLogs();
//        } else {
//            Toast.makeText(this, "Call Log permission denied", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void fetchCallLogs() {
//        callList.clear();
//        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
//                null, null, null, CallLog.Calls.DATE + " DESC");
//
//        int numberCol = cursor.getColumnIndex(CallLog.Calls.NUMBER);
//        int durationCol = cursor.getColumnIndex(CallLog.Calls.DURATION);
//        int typeCol = cursor.getColumnIndex(CallLog.Calls.TYPE);
//        int dateCol = cursor.getColumnIndex(CallLog.Calls.DATE);
//
//        while (cursor.moveToNext()) {
//            String number = cursor.getString(numberCol);
//            String duration = cursor.getString(durationCol);
//            String type = getCallType(cursor.getInt(typeCol));
//            long date = cursor.getLong(dateCol);
//
//            callList.add(new CallModel(number, duration, type, date));
//        }
//
//        cursor.close();
//        adapter.notifyDataSetChanged();
//    }
//
//    private String getCallType(int type) {
//        switch (type) {
//            case CallLog.Calls.INCOMING_TYPE:
//                return "Incoming";
//            case CallLog.Calls.OUTGOING_TYPE:
//                return "Outgoing";
//            case CallLog.Calls.MISSED_TYPE:
//                return "Missed";
//            default:
//                return "Unknown";
//        }
//    }
//}
