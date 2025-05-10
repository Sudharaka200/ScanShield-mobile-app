package com.example.scanshield_mobile_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder> {

    private List<CallModel> callList;

    public CallAdapter(List<CallModel> callList) {
        this.callList = callList;
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_item, parent, false);
        return new CallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
        CallModel call = callList.get(position);
        holder.tvNumber.setText("Number: " + call.getNumber());
        holder.tvType.setText("Type: " + call.getType());
        holder.tvDate.setText("Date: " + call.getDate());
        holder.tvDuration.setText("Duration: " + call.getDuration() + " seconds");
        holder.tvSpam.setText(call.isSpam() ? "⚠️ Spam" : "✔️ Not Spam");

    }

    @Override
    public int getItemCount() {
        return callList.size();
    }

    public static class CallViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvType, tvDate, tvDuration, tvSpam;

        public CallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvType = itemView.findViewById(R.id.tvType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvSpam = itemView.findViewById(R.id.tvSpam);
        }
    }
}
