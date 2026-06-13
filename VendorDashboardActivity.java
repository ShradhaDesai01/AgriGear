package com.example.agrigear;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VendorDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_dashboard);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Vendor Dashboard");

        CardView cardCreateEquipment = findViewById(R.id.cardCreateEquipment);
        CardView cardUpdateEquipment = findViewById(R.id.cardUpdateEquipment);
        CardView cardUpdateBooking = findViewById(R.id.cardUpdateBooking);

        CardView cardMyProfile = findViewById(R.id.cardMyProfile);

        cardCreateEquipment.setOnClickListener(v -> {
            Intent intent = new Intent(VendorDashboardActivity.this, CreateEquipmentActivity.class);
            startActivity(intent);
        });
        cardUpdateEquipment.setOnClickListener(v -> {
            Intent intent = new Intent(VendorDashboardActivity.this, UpdateEquipmentActivity.class);
            startActivity(intent);
        });
        cardUpdateBooking.setOnClickListener(v -> {
            Intent intent = new Intent(VendorDashboardActivity.this, VendorBookingActivity.class);
            startActivity(intent);
        });

        cardMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(VendorDashboardActivity.this, VendorProfileActivity.class);
            startActivity(intent);
        });
    }


} 