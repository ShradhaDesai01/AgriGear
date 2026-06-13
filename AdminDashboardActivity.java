package com.example.agrigear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.cardview.widget.CardView;

public class AdminDashboardActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AdminPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Dashboard");

        // Set up logout FAB
        FloatingActionButton fabLogout = findViewById(R.id.fabLogout);
        fabLogout.setOnClickListener(v -> logout());

        CardView cardApproveEquipment = findViewById(R.id.cardApproveEquipment);
        CardView cardUserDetails = findViewById(R.id.cardUserDetails);
        CardView cardVendorDetails = findViewById(R.id.cardVendorDetails);

        cardApproveEquipment.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ApproveEquipmentActivity.class);
            startActivity(intent);
        });
        cardUserDetails.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ViewUserDetailsActivity.class);
            startActivity(intent);
        });
        cardVendorDetails.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ViewVendorDetailsActivity.class);
            startActivity(intent);
        });
    }

    private void logout() {
        // Clear admin login state
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate back to admin login
        Intent intent = new Intent(this, adminlogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    }
