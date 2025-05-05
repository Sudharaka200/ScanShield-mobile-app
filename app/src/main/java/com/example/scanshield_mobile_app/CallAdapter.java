package com.example.scanshield_mobile_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder> {

    private Context context;
    private List<CallModel> callList;

    public CallAdapter(Context context, List<CallModel> callList) {
        this.context = context;
        this.callList = callList != null ? callList : new ArrayList<>();
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_call_log, parent, false);
        return new CallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
        CallModel call = callList.get(position);
        if (call == null) return;

        if (holder.tvNumber != null) {
            holder.tvNumber.setText(call.getNumber() != null ? call.getNumber() : "Unknown");
        }

        if (holder.tvDuration != null) {
            holder.tvDuration.setText(call.getDuration() != null ? "Duration: " + call.getDuration() + " sec" : "Duration: Unknown");
        }

        if (holder.tvDate != null) {
            try {
                String dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(new Date(call.getDate()));
                holder.tvDate.setText(dateStr);
            } catch (Exception e) {
                holder.tvDate.setText("Date: Unknown");
            }
        }

        if (holder.radioGroupStatus != null) {
            holder.radioGroupStatus.setOnCheckedChangeListener(null); // Clear previous listener
            holder.radioGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
                String status = "";
                if (checkedId == R.id.rbSpam) {
                    status = "spam";
                } else if (checkedId == R.id.rbNotSpam) {
                    status = "not_spam";
                }

                if (!status.isEmpty()) {
                    String callId = call.getNumber() != null
                            ? call.getNumber().replace("+", "").replace("-", "").replace(" ", "")
                            : String.valueOf(position);
                    FirebaseDatabase.getInstance().getReference("calls").child("call_" + callId)
                            .child("status").setValue(status)
                            .addOnCompleteListener(task -> {
                                String msg = task.isSuccessful() ? "Updated" : "Failed";
                                Toast.makeText(context, "Status " + msg, Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return callList.size();
    }

    public static class CallViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvDuration, tvDate;
        RadioGroup radioGroupStatus;
        RadioButton rbSpam, rbNotSpam;

        public CallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDate = itemView.findViewById(R.id.tvDate);
            radioGroupStatus = itemView.findViewById(R.id.radioGroupStatus);
            rbSpam = itemView.findViewById(R.id.rbSpam);
            rbNotSpam = itemView.findViewById(R.id.rbNotSpam);
            if (tvNumber == null || tvDuration == null || tvDate == null) {
                Log.e("CallViewHolder", "One or more views not found in item_call_log.xml");
            }
        }
    }
}