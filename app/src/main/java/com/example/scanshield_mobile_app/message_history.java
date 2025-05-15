package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class message_history extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView logedUser;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private MessageHistoryAdapter messageHistoryAdapter;
    private List<message_F> messageList;
    private TextView fullMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_history);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                .getReference("messageData");

        // Initialize UI components
        recyclerView = findViewById(R.id.message_history_recycler);
        fullMessageView = findViewById(R.id.full_message_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        messageHistoryAdapter = new MessageHistoryAdapter(messageList);
        recyclerView.setAdapter(messageHistoryAdapter);

        userCheck();
        fetchMessages();

        // Handle intent from popup/notification
        Intent intent = getIntent();
        if (intent.hasExtra("sender")) {
            String sender = intent.getStringExtra("sender");
            String message = intent.getStringExtra("message");
            long timestamp = intent.getLongExtra("timestamp", 0);
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(timestamp));
            fullMessageView.setText("From: " + sender + "\nTime: " + formattedDate + "\n\n" + message);
            fullMessageView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Prevent any item from being pre-selected
        bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }

        // Set the item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(message_history.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(message_history.this, SettingsActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(message_history.this, profile.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void userCheck() {
        mAuth = FirebaseAuth.getInstance();
        logedUser = findViewById(R.id.logUserEmailMessageHistory);
        user = mAuth.getCurrentUser();

        if (user == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            logedUser.setText(user.getEmail());
        }
    }

    private void fetchMessages() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.orderByChild("email").equalTo(user.getEmail())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            message_F message = dataSnapshot.getValue(message_F.class);
                            if (message != null) {
                                message.setKey(dataSnapshot.getKey()); // Set the Firebase key
                                messageList.add(message);
                            }
                        }
                        messageHistoryAdapter.notifyDataSetChanged();
                        if (messageList.isEmpty()) {
                            Toast.makeText(message_history.this, "No messages found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(message_history.this, "Failed to load messages: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class MessageHistoryAdapter extends RecyclerView.Adapter<MessageHistoryAdapter.MessageViewHolder> {
        private final List<message_F> messages;

        public MessageHistoryAdapter(List<message_F> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            message_F message = messages.get(position);
            holder.phoneNumber.setText(message.getPhoneNumber());
            holder.messageText.setText(message.getMessage());
            holder.dateTime.setText(message.getDateTime());

            // Check if the message is spam and apply red color with spam alert
            if (message.getIsSpam()) {
                holder.messageText.setTextColor(Color.RED);
                holder.spamAlert.setText("!"); // Red exclamation mark as spam alert
                holder.spamAlert.setTextColor(Color.RED);
                holder.spamAlert.setVisibility(View.VISIBLE);
            } else {
                holder.messageText.setTextColor(Color.BLACK); // Default color
                holder.spamAlert.setVisibility(View.GONE);
            }

            // Check if the message is unread
            if (!message.isRead()) {
                holder.phoneNumber.setTextColor(Color.BLUE); // Highlight unread messages
            } else {
                holder.phoneNumber.setTextColor(Color.BLACK); // Default color for read messages
            }

            // Set click listener to open full message
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(message_history.this, FullMessageActivity.class);
                intent.putExtra("phoneNumber", message.getPhoneNumber());
                intent.putExtra("message", message.getMessage());
                intent.putExtra("dateTime", message.getDateTime());
                intent.putExtra("isSpam", message.getIsSpam());
                intent.putExtra("read", message.isRead());
                intent.putExtra("replies", message.getReplies());
                intent.putExtra("messageKey", message.getKey()); // Use the stored key
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView phoneNumber, messageText, dateTime, spamAlert;

            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);
                phoneNumber = itemView.findViewById(R.id.phone_number);
                messageText = itemView.findViewById(R.id.message_text);
                dateTime = itemView.findViewById(R.id.date_time);
                spamAlert = itemView.findViewById(R.id.spam_alert);
            }
        }
    }
}