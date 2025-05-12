package com.example.scanshield_mobile_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder> {

    private Context context;
    private List<CallLogModel> callLogs;
    private DatabaseReference databaseReference;
    private static final String TAG = "CallAdapter";

    public CallAdapter(Context context, List<CallLogModel> callLogs) {
        this.context = context;
        this.callLogs = callLogs;
        databaseReference = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                .getReference("SpamNumbers");
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.call_item, parent, false);
        return new CallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
        CallLogModel model = callLogs.get(position);

        holder.callerName.setText(model.getName());
        holder.callerNumber.setText(model.getNumber());
        holder.callerType.setText(model.getType());
        holder.callTime.setText(model.getTime());

        // Normalize phone number
        String normalizedNumber = normalizePhoneNumber(model.getNumber());

        // Load current status from Firebase
        if (normalizedNumber != null) {
            databaseReference.child(normalizedNumber).child("status").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String status = task.getResult().getValue(String.class);
                    holder.statusText.setText("Status: " + (status != null ? status : "unknown"));
                    model.setStatus(status != null ? status : "unknown");
                } else {
                    holder.statusText.setText("Status: unknown");
                    model.setStatus("unknown");
                    Log.e(TAG, "Failed to read status for " + normalizedNumber + ": " +
                            (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                }
            });
        } else {
            holder.statusText.setText("Status: unknown");
            model.setStatus("unknown");
        }

        // Spam Button Click
        holder.spamButton.setOnClickListener(v -> {
            if (normalizedNumber != null) {
                databaseReference.child(normalizedNumber).child("status").setValue("spam")
                        .addOnSuccessListener(aVoid -> {
                            model.setStatus("spam");
                            holder.statusText.setText("Status: spam");
                            Log.d(TAG, "Successfully marked " + normalizedNumber + " as spam");
                        })
                        .addOnFailureListener(e -> {
                            holder.statusText.setText("Status: error");
                            Log.e(TAG, "Failed to mark " + normalizedNumber + " as spam: " + e.getMessage());
                        });
            } else {
                holder.statusText.setText("Status: invalid number");
                Log.e(TAG, "Cannot mark as spam: number is null or invalid");
            }
        });

        // Not Spam Button Click
        holder.notSpamButton.setOnClickListener(v -> {
            if (normalizedNumber != null) {
                databaseReference.child(normalizedNumber).child("status").setValue("not_spam")
                        .addOnSuccessListener(aVoid -> {
                            model.setStatus("not_spam");
                            holder.statusText.setText("Status: not_spam");
                            Log.d(TAG, "Successfully marked " + normalizedNumber + " as not_spam");
                        })
                        .addOnFailureListener(e -> {
                            holder.statusText.setText("Status: error");
                            Log.e(TAG, "Failed to mark " + normalizedNumber + " as not_spam: " + e.getMessage());
                        });
            } else {
                holder.statusText.setText("Status: invalid number");
                Log.e(TAG, "Cannot mark as not_spam: number is null or invalid");
            }
        });
    }

    @Override
    public int getItemCount() {
        return callLogs.size();
    }

    // Normalize phone number to ensure consistency
    private String normalizePhoneNumber(String number) {
        if (number == null || number.isEmpty()) return null;
        // Remove country code, spaces, dashes, and parentheses
        String normalized = number.replaceAll("[^0-9]", "");
        // Optionally, remove leading country code (e.g., +1 for US)
        if (normalized.startsWith("1") && normalized.length() > 10) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    public static class CallViewHolder extends RecyclerView.ViewHolder {
        TextView callerName, callerNumber, callerType, callTime, statusText;
        Button spamButton, notSpamButton;

        public CallViewHolder(@NonNull View itemView) {
            super(itemView);
            callerName = itemView.findViewById(R.id.textViewCallerName);
            callerNumber = itemView.findViewById(R.id.textViewCallerNumber);
            callerType = itemView.findViewById(R.id.textViewCallType);
            callTime = itemView.findViewById(R.id.textViewCallTime);
            statusText = itemView.findViewById(R.id.textViewStatus);
            spamButton = itemView.findViewById(R.id.buttonSpam);
            notSpamButton = itemView.findViewById(R.id.buttonNotSpam);
        }
    }
}