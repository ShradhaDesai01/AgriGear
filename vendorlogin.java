package com.example.agrigear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import com.google.firebase.firestore.FirebaseFirestore;

public class vendorlogin extends AppCompatActivity {

    EditText email, password;
    Button btnLogin;
    TextView registerLink, txtForgot;
    CheckBox checkRemember;
    ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String PREFS_NAME = "VendorPrefs";

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendorlogin);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Test database connection
        mDatabase.child("test").setValue("connected")
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Database connected successfully", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Database connection failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        registerLink = findViewById(R.id.registerLink);
        txtForgot = findViewById(R.id.txtForgot);
        checkRemember = findViewById(R.id.checkRemember);
        progressBar = findViewById(R.id.progressBar);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Load saved email/password
        loadRememberedCredentials();

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

        btnLogin.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        String vendorId = mAuth.getCurrentUser().getUid();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("vendors").document(vendorId).get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                if (checkRemember.isChecked()) {
                                    editor.putString("email", userEmail);
                                    editor.putString("password", userPassword);
                                    editor.putBoolean("remember", true);
                                    editor.apply();
                                } else {
                                    editor.clear().apply();
                                }
                                Intent intent = new Intent(vendorlogin.this, VendorDashboardActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(vendorlogin.this, "Account not properly registered. Please register again.", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                                startActivity(new Intent(vendorlogin.this, VendorRegisterActivity.class));
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(vendorlogin.this, "Failed to check registration: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    } else {
                        Toast.makeText(vendorlogin.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, VendorRegisterActivity.class));
        });

        txtForgot.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            if (TextUtils.isEmpty(userEmail)) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(vendorlogin.this, "Password reset email sent",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(vendorlogin.this, "Failed to send reset email: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        });
    }

    private void loadRememberedCredentials() {
        boolean remember = sharedPreferences.getBoolean("remember", false);
        if (remember) {
            email.setText(sharedPreferences.getString("email", ""));
            password.setText(sharedPreferences.getString("password", ""));
            checkRemember.setChecked(true);
        }
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
}
