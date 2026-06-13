package com.example.agrigear;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

public class UserDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Dashboard");

        CardView cardSearchEquipment = findViewById(R.id.cardSearchEquipment);
        CardView cardMyBooking = findViewById(R.id.cardMyBooking);
        CardView cardBookingHistory = findViewById(R.id.cardBookingHistory);
        CardView cardMyProfile = findViewById(R.id.cardMyProfile);

        cardSearchEquipment.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, SearchEquipmentActivity.class);
            startActivity(intent);
        });
        cardMyBooking.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, MyBookingActivity.class);
            startActivity(intent);
        });
        cardMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
        cardBookingHistory.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, BookingHistoryActivity.class);
            startActivity(intent);
        });
    }
} 