package com.example.scanshield_mobile_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity {
    private static final String TAG = "Home";
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private TextView lUser;
    private FirebaseUser user;
    private RecyclerView spamCallsRecyclerView;
    private RecyclerView callHistoryRecyclerView;
    private RecyclerView messageHistoryRecyclerView;
    private List<SpamCallItem> spamCallsList;
    private List<CallHistoryItem> callHistoryList;
    private List<MessageHistoryItem> messageHistoryList;
    private SpamCallsAdapter spamCallsAdapter;
    private CallHistoryAdapter callHistoryAdapter;
    private MessageHistoryAdapter messageHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Firebase setup
        firebaseDatabase = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("messageData");

        // Initialize UI components
        spamCallsRecyclerView = findViewById(R.id.spam_calls_list);
        callHistoryRecyclerView = findViewById(R.id.call_history_list);
        messageHistoryRecyclerView = findViewById(R.id.message_history_list);

        spamCallsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        callHistoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        messageHistoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));

        spamCallsList = new ArrayList<>();
        callHistoryList = new ArrayList<>();
        messageHistoryList = new ArrayList<>();

        spamCallsAdapter = new SpamCallsAdapter(spamCallsList);
        callHistoryAdapter = new CallHistoryAdapter(callHistoryList);
        messageHistoryAdapter = new MessageHistoryAdapter(messageHistoryList);

        spamCallsRecyclerView.setAdapter(spamCallsAdapter);
        callHistoryRecyclerView.setAdapter(callHistoryAdapter);
        messageHistoryRecyclerView.setAdapter(messageHistoryAdapter);

        // Initialize UI and navigation
        userCheck();
        navigationButtons();
        setupBottomNavigation();

        // Load data
        loadBlockedCalls();
        loadCallHistory();
        loadMessageHistory();

        // Request permissions if needed
        checkUserPermission();
    }

    private void userCheck() {
        mAuth = FirebaseAuth.getInstance();
        lUser = findViewById(R.id.LogUserEmailHome);
        user = mAuth.getCurrentUser();

        if (user == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            lUser.setText(user.getEmail());
        }
    }

    private void navigationButtons() {
        LinearLayout buttonDialPad = findViewById(R.id.dialpad_home);
        buttonDialPad.setOnClickListener(v -> startActivity(new Intent(this, dialpad.class)));

        LinearLayout buttonSearch = findViewById(R.id.search_home);
        buttonSearch.setOnClickListener(v -> startActivity(new Intent(this, number_search.class)));

        LinearLayout buttonCalls = findViewById(R.id.calls_home);
        buttonCalls.setOnClickListener(v -> startActivity(new Intent(this, CallHistoryActivity.class)));

        LinearLayout buttonMessages = findViewById(R.id.messages_home);
        buttonMessages.setOnClickListener(v -> startActivity(new Intent(this, message_history.class)));

        LinearLayout buttonBlocked = findViewById(R.id.blocked_home);
        buttonBlocked.setOnClickListener(v -> startActivity(new Intent(this, blocked_numbers.class)));

        LinearLayout buttonSpam = findViewById(R.id.spam_home);
        buttonSpam.setOnClickListener(v -> startActivity(new Intent(this, spam_messages.class)));

        LinearLayout buttonSettings = findViewById(R.id.setting_home);
        buttonSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, profile.class));
                return true;
            }
            return false;
        });
    }

    private void addDataToFirebase(String phoneNumber, String message) {
        if (user == null) {
            Toast.makeText(this, "User not logged in. Cannot save data.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store dateTime as a Unix timestamp (milliseconds)
        long currentDateTime = System.currentTimeMillis();

        message_F message_f = new message_F();
        message_f.setEmail(user.getEmail());
        message_f.setPhoneNumber(phoneNumber);
        message_f.setMessage(message);
        message_f.setDateTime(String.valueOf(currentDateTime));

        databaseReference.push().setValue(message_f)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Data added successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void checkUserPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_CALL_LOG
            }, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    private void loadBlockedCalls() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "READ_CALL_LOG permission not granted");
            Toast.makeText(this, "Call log permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] projection = new String[]{
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.TYPE
        };
        String selection = CallLog.Calls.TYPE + "=?";
        String[] selectionArgs = new String[]{String.valueOf(CallLog.Calls.BLOCKED_TYPE)};

        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                CallLog.Calls.DATE + " DESC"
        );

        spamCallsList.clear();
        if (cursor != null) {
            int count = 0;
            while (cursor.moveToNext() && count < 10) {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                long date = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(date));
                spamCallsList.add(new SpamCallItem(number, formattedDate));
                count++;
                Log.d(TAG, "Added blocked call: " + number + " - " + formattedDate);
            }
            cursor.close();
            spamCallsAdapter.notifyDataSetChanged();
            Log.d(TAG, "Spam Calls List Size: " + spamCallsList.size());
            if (spamCallsList.isEmpty()) {
                Log.w(TAG, "No blocked calls found in call log");
                spamCallsList.add(new SpamCallItem("No blocked calls found", ""));
                spamCallsAdapter.notifyDataSetChanged();
            }
        } else {
            Log.e(TAG, "Cursor is null when querying call log");
            spamCallsList.add(new SpamCallItem("Error loading blocked calls", ""));
            spamCallsAdapter.notifyDataSetChanged();
        }
    }

    private void loadCallHistory() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "READ_CALL_LOG permission not granted");
            Toast.makeText(this, "Call log permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] projection = new String[]{
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.TYPE
        };

        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                CallLog.Calls.DATE + " DESC"
        );

        callHistoryList.clear();
        if (cursor != null) {
            int count = 0;
            while (cursor.moveToNext() && count < 10) {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                long date = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(date));
                callHistoryList.add(new CallHistoryItem(number, formattedDate));
                count++;
                Log.d(TAG, "Added call history: " + number + " - " + formattedDate);
            }
            cursor.close();
            callHistoryAdapter.notifyDataSetChanged();
            Log.d(TAG, "Call History List Size: " + callHistoryList.size());
            if (callHistoryList.isEmpty()) {
                Log.w(TAG, "No call history found in call log, adding dummy data");
                callHistoryList.add(new CallHistoryItem("1234567890", "14/05/2025 14:30"));
                callHistoryList.add(new CallHistoryItem("0987654321", "14/05/2025 14:31"));
                callHistoryList.add(new CallHistoryItem("5555555555", "14/05/2025 14:32"));
                callHistoryAdapter.notifyDataSetChanged();
            }
        } else {
            Log.e(TAG, "Cursor is null when querying call log");
            callHistoryList.add(new CallHistoryItem("Error loading call history", ""));
            callHistoryAdapter.notifyDataSetChanged();
        }
    }

    private void loadMessageHistory() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.orderByChild("email").equalTo(user.getEmail())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageHistoryList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            message_F message = dataSnapshot.getValue(message_F.class);
                            if (message != null) {
                                String dateTime = message.getDateTime();
                                try {
                                    Date date;
                                    if (dateTime.matches("\\d+")) {
                                        long timestamp = Long.parseLong(dateTime);
                                        date = new Date(timestamp);
                                    } else {
                                        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateTime);
                                    }
                                    String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date);
                                    messageHistoryList.add(new MessageHistoryItem(message.getPhoneNumber(), formattedDate));
                                } catch (ParseException | NumberFormatException e) {
                                    Log.e(TAG, "Failed to parse dateTime: " + dateTime, e);
                                    messageHistoryList.add(new MessageHistoryItem(message.getPhoneNumber(), "Invalid date"));
                                }
                            }
                        }
                        messageHistoryAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Message History List Size: " + messageHistoryList.size());
                        if (messageHistoryList.isEmpty()) {
                            Log.w(TAG, "No message history found in Firebase, adding dummy data");
                            messageHistoryList.add(new MessageHistoryItem("1234567890", "14/05/2025 14:30"));
                            messageHistoryList.add(new MessageHistoryItem("0987654321", "14/05/2025 14:31"));
                            messageHistoryList.add(new MessageHistoryItem("5555555555", "14/05/2025 14:32"));
                            messageHistoryAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to load message history: " + error.getMessage());
                        messageHistoryList.add(new MessageHistoryItem("Error loading message history", ""));
                        messageHistoryAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS && grantResults.length > 0) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
                loadBlockedCalls();
                loadCallHistory();
            } else {
                Toast.makeText(this, "Permissions denied. Cannot load call data.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Custom classes to hold data
    private static class SpamCallItem {
        private final String number;
        private final String time;

        public SpamCallItem(String number, String time) {
            this.number = number;
            this.time = time;
        }

        public String getNumber() {
            return number;
        }

        public String getTime() {
            return time;
        }
    }

    private static class CallHistoryItem {
        private final String number;
        private final String time;

        public CallHistoryItem(String number, String time) {
            this.number = number;
            this.time = time;
        }

        public String getNumber() {
            return number;
        }

        public String getTime() {
            return time;
        }
    }

    private static class MessageHistoryItem {
        private final String number;
        private final String time;

        public MessageHistoryItem(String number, String time) {
            this.number = number;
            this.time = time;
        }

        public String getNumber() {
            return number;
        }

        public String getTime() {
            return time;
        }
    }

    // Adapters for RecyclerView
    private class SpamCallsAdapter extends RecyclerView.Adapter<SpamCallsAdapter.SpamCallViewHolder> {
        private final List<SpamCallItem> spamCalls;

        public SpamCallsAdapter(List<SpamCallItem> spamCalls) {
            this.spamCalls = spamCalls;
        }

        @NonNull
        @Override
        public SpamCallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spam_call_home, parent, false);
            return new SpamCallViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SpamCallViewHolder holder, int position) {
            SpamCallItem item = spamCalls.get(position);
            holder.numberTextView.setText(item.getNumber());
            holder.timeTextView.setText(item.getTime());
        }

        @Override
        public int getItemCount() {
            return spamCalls.size();
        }

        public class SpamCallViewHolder extends RecyclerView.ViewHolder {
            TextView numberTextView;
            TextView timeTextView;

            public SpamCallViewHolder(@NonNull View itemView) {
                super(itemView);
                numberTextView = itemView.findViewById(R.id.spam_call_number);
                timeTextView = itemView.findViewById(R.id.spam_call_time);
            }
        }
    }

    private class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.CallHistoryViewHolder> {
        private final List<CallHistoryItem> callHistory;

        public CallHistoryAdapter(List<CallHistoryItem> callHistory) {
            this.callHistory = callHistory;
        }

        @NonNull
        @Override
        public CallHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_history_home, parent, false);
            return new CallHistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CallHistoryViewHolder holder, int position) {
            CallHistoryItem item = callHistory.get(position);
            holder.numberTextView.setText(item.getNumber());
            holder.timeTextView.setText(item.getTime());
        }

        @Override
        public int getItemCount() {
            return callHistory.size();
        }

        public class CallHistoryViewHolder extends RecyclerView.ViewHolder {
            TextView numberTextView;
            TextView timeTextView;

            public CallHistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                numberTextView = itemView.findViewById(R.id.call_history_number);
                timeTextView = itemView.findViewById(R.id.call_history_time);
            }
        }
    }

    private class MessageHistoryAdapter extends RecyclerView.Adapter<MessageHistoryAdapter.MessageHistoryViewHolder> {
        private final List<MessageHistoryItem> messageHistory;

        public MessageHistoryAdapter(List<MessageHistoryItem> messageHistory) {
            this.messageHistory = messageHistory;
        }

        @NonNull
        @Override
        public MessageHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_history_home, parent, false);
            return new MessageHistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageHistoryViewHolder holder, int position) {
            MessageHistoryItem item = messageHistory.get(position);
            holder.numberTextView.setText(item.getNumber());
            holder.timeTextView.setText(item.getTime());
        }

        @Override
        public int getItemCount() {
            return messageHistory.size();
        }

        public class MessageHistoryViewHolder extends RecyclerView.ViewHolder {
            TextView numberTextView;
            TextView timeTextView;

            public MessageHistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                numberTextView = itemView.findViewById(R.id.message_history_number);
                timeTextView = itemView.findViewById(R.id.message_history_time);
            }
        }
    }
}