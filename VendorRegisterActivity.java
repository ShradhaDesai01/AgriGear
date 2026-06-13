package com.example.agrigear;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Patterns;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.view.MotionEvent;
import android.view.View;

public class VendorRegisterActivity extends Activity {

    EditText name, email, password, mobile, city, address, uniqueId;
    Button btnSignUp;
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mobile = findViewById(R.id.mobile);
        city = findViewById(R.id.city);
        address = findViewById(R.id.address);
        uniqueId = findViewById(R.id.uniqueId);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Set up password visibility toggle
        password.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        btnSignUp.setOnClickListener(v -> {
            if (!validateInputs()) return;

            String uniqueIdStr = uniqueId.getText().toString().trim();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("vendors").whereEqualTo("uniqueId", uniqueIdStr).get().addOnSuccessListener(querySnapshot -> {
                if (!querySnapshot.isEmpty()) {
                    uniqueId.setError("Not available");
                } else {
                    uniqueId.setError(null);
                    registerVendor();
                }
            });
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(android.R.drawable.ic_lock_lock),
                null,
                getResources().getDrawable(android.R.drawable.ic_menu_view),
                null
            );
        } else {
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(android.R.drawable.ic_lock_lock),
                null,
                getResources().getDrawable(android.R.drawable.ic_menu_view),
                null
            );
        }
        isPasswordVisible = !isPasswordVisible;
        password.setSelection(password.getText().length());
    }

    private boolean validateInputs() {
        // Reset errors
        name.setError(null);
        email.setError(null);
        password.setError(null);
        mobile.setError(null);
        city.setError(null);
        address.setError(null);
        uniqueId.setError(null);

        // Get values
        String nameStr = name.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();
        String mobileStr = mobile.getText().toString().trim();
        String cityStr = city.getText().toString().trim();
        String addressStr = address.getText().toString().trim();
        String uniqueIdStr = uniqueId.getText().toString().trim();

        // Validate name
        if (TextUtils.isEmpty(nameStr)) {
            name.setError("Name is required");
            return false;
        }

        // Validate email
        if (TextUtils.isEmpty(emailStr)) {
            email.setError("Email is required");
            return false;
        }
        if (!emailStr.contains("@")) {
            email.setError("Email must contain @ symbol");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("Enter a valid email address");
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(passwordStr)) {
            password.setError("Password is required");
            return false;
        }
        if (passwordStr.length() < 6) {
            password.setError("Password must be at least 6 characters");
            return false;
        }
        
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasSpecialChar = false;
        boolean hasNumber = false;
        
        for (char c : passwordStr.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            if (Character.isLowerCase(c)) hasLowerCase = true;
            if (!Character.isLetterOrDigit(c)) hasSpecialChar = true;
            if (Character.isDigit(c)) hasNumber = true;
        }
        
        if (!hasUpperCase) {
            password.setError("Password must contain at least one uppercase letter");
            return false;
        }
        if (!hasLowerCase) {
            password.setError("Password must contain at least one lowercase letter");
            return false;
        }
        if (!hasSpecialChar) {
            password.setError("Password must contain at least one special character");
            return false;
        }
        if (!hasNumber) {
            password.setError("Password must contain at least one number");
            return false;
        }

        // Validate mobile
        if (TextUtils.isEmpty(mobileStr)) {
            mobile.setError("Mobile number is required");
            return false;
        }
        if (mobileStr.length() < 10) {
            mobile.setError("Enter a valid mobile number");
            return false;
        }

        // Validate city
        if (TextUtils.isEmpty(cityStr)) {
            city.setError("City is required");
            return false;
        }

        // Validate address
        if (TextUtils.isEmpty(addressStr)) {
            address.setError("Address is required");
            return false;
        }

        // Validate uniqueId
        if (TextUtils.isEmpty(uniqueIdStr)) {
            uniqueId.setError("Unique ID is required");
            return false;
        }
        if (!uniqueIdStr.matches("\\d{4}")) {
            uniqueId.setError("Enter a unique 4-digit number");
            return false;
        }

        return true;
    }

    private void registerVendor() {
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration success, save additional vendor data
                        String vendorId = mAuth.getCurrentUser().getUid();
                        saveVendorData(vendorId);
                    } else {
                        // Registration failed
                        Toast.makeText(VendorRegisterActivity.this, 
                            "Registration failed: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveVendorData(String vendorId) {
        Vendor vendor = new Vendor(
            name.getText().toString().trim(),
            email.getText().toString().trim(),
            mobile.getText().toString().trim(),
            city.getText().toString().trim(),
            address.getText().toString().trim(),
            uniqueId.getText().toString().trim()
        );

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vendors").document(vendorId).set(vendor)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(VendorRegisterActivity.this,
                        "Registration successful!",
                        Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(VendorRegisterActivity.this,
                        "Failed to save vendor data: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                });
    }
}
