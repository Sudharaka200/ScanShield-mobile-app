package com.example.scanshield_mobile_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

public class spam_calls extends AppCompatActivity {

    private static final String TAG = "spam_calls";
    private FirebaseAuth mAuth;
    private TextView logedUser;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private SpamCallAdapter spamCallAdapter;
    private List<call_F> spamCallList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spam_calls);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                .getReference("callData");

        // Initialize UI components
        recyclerView = findViewById(R.id.spam_calls_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spamCallList = new ArrayList<>();
        spamCallAdapter = new SpamCallAdapter(spamCallList);
        recyclerView.setAdapter(spamCallAdapter);

        userCheck();;
        fetchSpamCalls();

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
                    startActivity(new Intent(spam_calls.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(spam_calls.this, SettingsActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(spam_calls.this, profile.class));
                    return true;
                }
                return false;
            }
        });

    }

    private void userCheck() {
        mAuth = FirebaseAuth.getInstance();
        logedUser = findViewById(R.id.logUserEmailSpamCalls);
        user = mAuth.getCurrentUser();

        if (user == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            logedUser.setText(user.getEmail());
        }
    }

    private void fetchSpamCalls() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.orderByChild("email").equalTo(user.getEmail())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        spamCallList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            call_F call = dataSnapshot.getValue(call_F.class);
                            if (call != null && Boolean.TRUE.equals(call.getIsSpam())) {
                                spamCallList.add(call);
                                Log.d(TAG, "Added spam call: " + call.getPhoneNumber() + " - " + call.getCallStatus());
                            }
                        }
                        spamCallAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Spam Call List Size: " + spamCallList.size());
                        if (spamCallList.isEmpty()) {
                            Toast.makeText(spam_calls.this, "No spam calls found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(spam_calls.this, "Failed to load spam calls: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to load spam calls: " + error.getMessage());
                    }
                });
    }

    private class SpamCallAdapter extends RecyclerView.Adapter<SpamCallAdapter.SpamCallViewHolder> {
        private final List<call_F> calls;

        public SpamCallAdapter(List<call_F> calls) {
            this.calls = calls;
        }

        @NonNull
        @Override
        public SpamCallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spam_call, parent, false);
            return new SpamCallViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SpamCallViewHolder holder, int position) {
            call_F call = calls.get(position);
            holder.phoneNumber.setText(call.getPhoneNumber());
            holder.callStatus.setText("Spam !"); // Set status as "Spam !" for clarity
            holder.callStatus.setTextColor(Color.RED);
            holder.dateTime.setText(call.getDateTime());
        }

        @Override
        public int getItemCount() {
            return calls.size();
        }

        public class SpamCallViewHolder extends RecyclerView.ViewHolder {
            TextView phoneNumber, callStatus, dateTime;

            public SpamCallViewHolder(@NonNull View itemView) {
                super(itemView);
                phoneNumber = itemView.findViewById(R.id.phone_number);
                callStatus = itemView.findViewById(R.id.call_status);
                dateTime = itemView.findViewById(R.id.date_time);
            }
        }
    }
}