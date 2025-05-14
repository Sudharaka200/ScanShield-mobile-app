package com.example.scanshield_mobile_app;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class blocked_numbers extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView logedUser; // Changed from lUser to match naming convention
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private BlockedNumbersAdapter blockedNumbersAdapter;
    private List<blocked_F> blockedNumberList; // Assuming a blocked_F model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_numbers);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance("https://scanshield-project-default-rtdb.firebaseio.com/")
                .getReference("blockedData");

        // Initialize UI components
        recyclerView = findViewById(R.id.blocked_numbers_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        blockedNumberList = new ArrayList<>();
        blockedNumbersAdapter = new BlockedNumbersAdapter(blockedNumberList);
        recyclerView.setAdapter(blockedNumbersAdapter);

        userCheck();
        setupBottomNavigation();
        fetchBlockedNumbers();
    }

    private void userCheck() {
        mAuth = FirebaseAuth.getInstance();
        logedUser = findViewById(R.id.logedUserEmailBlockedNumbers); // Adjusted ID
        user = mAuth.getCurrentUser();

        if (user == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            logedUser.setText(user.getEmail());
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(blocked_numbers.this, Home.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(blocked_numbers.this, SettingsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(blocked_numbers.this, profile.class));
                return true;
            }
            return false;
        });
    }

    private void fetchBlockedNumbers() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.orderByChild("email").equalTo(user.getEmail())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        blockedNumberList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            blocked_F blockedNumber = dataSnapshot.getValue(blocked_F.class);
                            if (blockedNumber != null) {
                                blockedNumberList.add(blockedNumber);
                            }
                        }
                        blockedNumbersAdapter.notifyDataSetChanged();
                        if (blockedNumberList.isEmpty()) {
                            Toast.makeText(blocked_numbers.this, "No blocked numbers found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(blocked_numbers.this, "Failed to load blocked numbers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class BlockedNumbersAdapter extends RecyclerView.Adapter<BlockedNumbersAdapter.BlockedNumberViewHolder> {
        private final List<blocked_F> blockedNumbers;

        public BlockedNumbersAdapter(List<blocked_F> blockedNumbers) {
            this.blockedNumbers = blockedNumbers;
        }

        @NonNull
        @Override
        public BlockedNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blocked_number, parent, false);
            return new BlockedNumberViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BlockedNumberViewHolder holder, int position) {
            blocked_F blockedNumber = blockedNumbers.get(position);
            holder.phoneNumber.setText(blockedNumber.getPhoneNumber());
        }

        @Override
        public int getItemCount() {
            return blockedNumbers.size();
        }

        public class BlockedNumberViewHolder extends RecyclerView.ViewHolder {
            TextView phoneNumber;

            public BlockedNumberViewHolder(@NonNull View itemView) {
                super(itemView);
                phoneNumber = itemView.findViewById(R.id.phone_number);
            }
        }
    }
}