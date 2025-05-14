package com.example.scanshield_mobile_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessagePopupActivity extends Activity {
    private TextView senderTextView;
    private TextView messageTextView;
    private Button markAsReadButton;
    private Button viewMessageButton;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_popup);

        // Set the popup window size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.4));

        // Initialize Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                .getReference("messageData");

        // Initialize UI components
        senderTextView = findViewById(R.id.popup_sender);
        messageTextView = findViewById(R.id.popup_message);
        markAsReadButton = findViewById(R.id.btn_mark_as_read);
        viewMessageButton = findViewById(R.id.btn_view_message);

        // Get message details from intent
        Intent intent = getIntent();
        String sender = intent.getStringExtra("sender");
        String message = intent.getStringExtra("message");
        long timestamp = intent.getLongExtra("timestamp", 0);

        // Format timestamp
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(timestamp));

        // Set message details
        senderTextView.setText("From: " + sender);
        messageTextView.setText(message.length() > 50 ? message.substring(0, 50) + "..." : message);

        // Mark as Read button
        markAsReadButton.setOnClickListener(v -> {
            markMessageAsRead(sender, timestamp);
            finish(); // Close the popup
        });

        // View Message button
        viewMessageButton.setOnClickListener(v -> {
            // Query Firebase to get the messageKey and other details
            Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        message_F messageObj = snapshot.getValue(message_F.class);
                        if (messageObj != null && messageObj.getPhoneNumber().equals(sender) &&
                                messageObj.getDateTime().equals(String.valueOf(timestamp))) {
                            // Mark the message as read
                            snapshot.getRef().child("read").setValue(true);

                            // Launch FullMessageActivity with message details
                            Intent viewIntent = new Intent(MessagePopupActivity.this, FullMessageActivity.class);
                            viewIntent.putExtra("phoneNumber", messageObj.getPhoneNumber());
                            viewIntent.putExtra("message", messageObj.getMessage());
                            viewIntent.putExtra("dateTime", messageObj.getDateTime());
                            viewIntent.putExtra("isSpam", messageObj.getIsSpam());
                            viewIntent.putExtra("read", messageObj.isRead());
                            viewIntent.putExtra("replies", messageObj.getReplies());
                            viewIntent.putExtra("messageKey", snapshot.getKey());
                            startActivity(viewIntent);
                            finish(); // Close the popup
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                    Toast.makeText(MessagePopupActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void markMessageAsRead(String sender, long timestamp) {
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    message_F message = snapshot.getValue(message_F.class);
                    if (message != null && message.getPhoneNumber().equals(sender) &&
                            message.getDateTime().equals(String.valueOf(timestamp))) {
                        snapshot.getRef().child("read").setValue(true);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Toast.makeText(MessagePopupActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}