package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FullMessageActivity extends AppCompatActivity {

    private TextView fullPhoneNumber, fullDateTime;
    private EditText replyEditText;
    private Button sendReplyButton;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String messageKey;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> chatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_message);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                .getReference("messageData");
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize UI components
        fullPhoneNumber = findViewById(R.id.full_phone_number);
        fullDateTime = findViewById(R.id.full_date_time);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        replyEditText = findViewById(R.id.reply_edit_text);
        sendReplyButton = findViewById(R.id.send_reply_button);

        // Setup RecyclerView
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Get intent extras
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String message = intent.getStringExtra("message");
        String dateTime = intent.getStringExtra("dateTime");
        Boolean isSpam = intent.getBooleanExtra("isSpam", false);
        Boolean read = intent.getBooleanExtra("read", false);
        String replies = intent.getStringExtra("replies");
        messageKey = intent.getStringExtra("messageKey");

        // Set message details
        fullPhoneNumber.setText("From: " + phoneNumber);
        fullDateTime.setText("Time: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(Long.parseLong(dateTime))));
        if (Boolean.TRUE.equals(isSpam)) {
            fullPhoneNumber.setTextColor(Color.RED);
        }
        if (Boolean.FALSE.equals(read)) {
            fullPhoneNumber.setTextColor(Color.BLUE);
        }

        // Populate chat messages
        chatMessages.add(new ChatMessage(message, false)); // Original message (received)
        if (replies != null && !replies.isEmpty()) {
            String[] replyLines = replies.split("\n");
            for (String reply : replyLines) {
                if (!reply.trim().isEmpty()) {
                    chatMessages.add(new ChatMessage(reply, true)); // Replies (sent)
                }
            }
        }
        chatAdapter.notifyDataSetChanged();

        // Send reply button click listener
        sendReplyButton.setOnClickListener(v -> {
            String reply = replyEditText.getText().toString().trim();
            if (!reply.isEmpty()) {
                String currentReplies = chatMessages.stream()
                        .filter(ChatMessage::isSent)
                        .map(ChatMessage::getText)
                        .reduce((a, b) -> a + "\n" + b)
                        .orElse("");
                String newReplies = currentReplies.isEmpty() ? reply : currentReplies + "\n- " + reply + " (Sent: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()) + ")";
                databaseReference.child(messageKey).child("replies").setValue(newReplies)
                        .addOnSuccessListener(aVoid -> {
                            chatMessages.add(new ChatMessage("- " + reply + " (Sent: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()) + ")", true));
                            chatAdapter.notifyDataSetChanged();
                            replyEditText.setText("");
                            Toast.makeText(FullMessageActivity.this, "Reply sent!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(FullMessageActivity.this, "Failed to send reply: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(FullMessageActivity.this, "Please enter a reply", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ChatMessage class to hold message data
    private static class ChatMessage {
        private String text;
        private boolean isSent;

        public ChatMessage(String text, boolean isSent) {
            this.text = text;
            this.isSent = isSent;
        }

        public String getText() {
            return text;
        }

        public boolean isSent() {
            return isSent;
        }
    }

    // ChatAdapter for RecyclerView
    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_bubble, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage chatMessage = chatMessages.get(position);
            holder.bubbleText.setText(chatMessage.getText());

            // Align bubble based on sent/received
            if (chatMessage.isSent()) {
                holder.bubbleCard.setLayoutParams(new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                ));
                ((ConstraintLayout.LayoutParams) holder.bubbleCard.getLayoutParams()).endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                ((ConstraintLayout.LayoutParams) holder.bubbleCard.getLayoutParams()).startToStart = ConstraintLayout.LayoutParams.UNSET;
                holder.bubbleCard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary, null)); // Green for sent
            } else {
                holder.bubbleCard.setLayoutParams(new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                ));
                ((ConstraintLayout.LayoutParams) holder.bubbleCard.getLayoutParams()).startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                ((ConstraintLayout.LayoutParams) holder.bubbleCard.getLayoutParams()).endToEnd = ConstraintLayout.LayoutParams.UNSET;
                holder.bubbleCard.setCardBackgroundColor(getResources().getColor(R.color.chatBubbleReceived, null)); // Gray for received
            }
        }

        @Override
        public int getItemCount() {
            return chatMessages.size();
        }

        public class ChatViewHolder extends RecyclerView.ViewHolder {
            CardView bubbleCard;
            TextView bubbleText;

            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                bubbleCard = itemView.findViewById(R.id.bubble_card);
                bubbleText = itemView.findViewById(R.id.bubble_text);
            }
        }
    }
}