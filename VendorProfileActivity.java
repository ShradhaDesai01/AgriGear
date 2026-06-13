package com.example.agrigear;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class VendorProfileActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, mobileEditText, cityEditText, addressEditText, uniqueIdEditText;
    private Button editButton, saveButton, cancelButton, changePasswordButton;
    private TextView titleText;
    private FirebaseAuth mAuth;
    private boolean isEditMode = false;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Vendor Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        initializeViews();
        loadVendorProfile();
        setupClickListeners();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        cityEditText = findViewById(R.id.cityEditText);
        addressEditText = findViewById(R.id.addressEditText);
        uniqueIdEditText = findViewById(R.id.uniqueIdEditText);
        
        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        // Initially disable editing
        setEditMode(false);
    }

    private void setupClickListeners() {
        editButton.setOnClickListener(v -> setEditMode(true));
        saveButton.setOnClickListener(v -> saveProfile());
        cancelButton.setOnClickListener(v -> {
            setEditMode(false);
            loadVendorProfile(); // Reload original data
        });
        changePasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void loadVendorProfile() {
        String vendorId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
        if (vendorId.isEmpty()) {
            Toast.makeText(this, "Vendor not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vendors").document(vendorId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Vendor vendor = documentSnapshot.toObject(Vendor.class);
                if (vendor != null) {
                    nameEditText.setText(vendor.getName());
                    emailEditText.setText(vendor.getEmail());
                    mobileEditText.setText(vendor.getMobile());
                    cityEditText.setText(vendor.getCity());
                    addressEditText.setText(vendor.getAddress());
                    uniqueIdEditText.setText(vendor.getUniqueId());
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(VendorProfileActivity.this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void setEditMode(boolean editMode) {
        isEditMode = editMode;
        
        if (editMode) {
            titleText.setText("Edit Profile");
            editButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            
            // Enable editing
            nameEditText.setEnabled(true);
            mobileEditText.setEnabled(true);
            cityEditText.setEnabled(true);
            addressEditText.setEnabled(true);
            uniqueIdEditText.setEnabled(false);
            
            // Email cannot be edited
            emailEditText.setEnabled(false);
        } else {
            titleText.setText("Vendor Profile");
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            
            // Disable editing
            nameEditText.setEnabled(false);
            emailEditText.setEnabled(false);
            mobileEditText.setEnabled(false);
            cityEditText.setEnabled(false);
            addressEditText.setEnabled(false);
            uniqueIdEditText.setEnabled(false);
        }
    }

    private void saveProfile() {
        String name = nameEditText.getText().toString().trim();
        String mobile = mobileEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String uniqueId = uniqueIdEditText.getText().toString().trim();

        if (name.isEmpty() || mobile.isEmpty() || city.isEmpty() || address.isEmpty() || uniqueId.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String vendorId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
        if (vendorId.isEmpty()) {
            Toast.makeText(this, "Vendor not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update vendor data
        Vendor updatedVendor = new Vendor(name, emailEditText.getText().toString().trim(), mobile, city, address, uniqueId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vendors").document(vendorId).set(updatedVendor)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                setEditMode(false);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Email not available", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Password reset email sent to " + email, Toast.LENGTH_LONG).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to send reset email: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 