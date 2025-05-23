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

import java.util.ArrayList;
import java.util.List;

public class spam_messages extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView logedUser;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private SpamMessageAdapter spamMessageAdapter;
    private List<message_F> spamMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spam_messages);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                .getReference("messageData");

        // Initialize UI components
        recyclerView = findViewById(R.id.spam_messages_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spamMessageList = new ArrayList<>();
        spamMessageAdapter = new SpamMessageAdapter(spamMessageList);
        recyclerView.setAdapter(spamMessageAdapter);

        userCheck();
        fetchSpamMessages();

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
                    startActivity(new Intent(spam_messages.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(spam_messages.this, SettingsActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(spam_messages.this, profile.class));
                    return true;
                }
                return false;
            }
        });

    }

    private void userCheck() {
        mAuth = FirebaseAuth.getInstance();
        logedUser = findViewById(R.id.logedUserEmailSpamMessages);
        user = mAuth.getCurrentUser();

        if (user == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            logedUser.setText(user.getEmail());
        }
    }

    private void fetchSpamMessages() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.orderByChild("email").equalTo(user.getEmail())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        spamMessageList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            message_F message = dataSnapshot.getValue(message_F.class);
                            if (message != null && Boolean.TRUE.equals(message.getIsSpam())) {
                                message.setKey(dataSnapshot.getKey()); // Set the Firebase key
                                spamMessageList.add(message);
                            }
                        }
                        spamMessageAdapter.notifyDataSetChanged();
                        if (spamMessageList.isEmpty()) {
                            Toast.makeText(spam_messages.this, "No spam messages found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(spam_messages.this, "Failed to load spam messages: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class SpamMessageAdapter extends RecyclerView.Adapter<SpamMessageAdapter.SpamMessageViewHolder> {
        private final List<message_F> messages;

        public SpamMessageAdapter(List<message_F> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public SpamMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new SpamMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SpamMessageViewHolder holder, int position) {
            message_F message = messages.get(position);
            holder.phoneNumber.setText(message.getPhoneNumber());
            holder.messageText.setText(message.getMessage());
            holder.dateTime.setText(message.getDateTime());
            // Color spam messages red
            holder.messageText.setTextColor(Color.RED);

            // Set click listener to open full message
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(spam_messages.this, FullMessageActivity.class);
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

        public class SpamMessageViewHolder extends RecyclerView.ViewHolder {
            TextView phoneNumber, messageText, dateTime, spamAlert;

            public SpamMessageViewHolder(@NonNull View itemView) {
                super(itemView);
                phoneNumber = itemView.findViewById(R.id.phone_number);
                messageText = itemView.findViewById(R.id.message_text);
                dateTime = itemView.findViewById(R.id.date_time);
                spamAlert = itemView.findViewById(R.id.spam_alert);
                if (spamAlert != null) {
                    spamAlert.setText("!");
                    spamAlert.setTextColor(Color.RED);
                    spamAlert.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}