package com.example.agrigear;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;

public class adminlogin extends AppCompatActivity {

    EditText email, password;
    Button btnLogin;
    ProgressBar progressBar;

    // Hardcoded admin credentials
    private static final String ADMIN_EMAIL = "admin@agrigear.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String PREFS_NAME = "AdminPrefs";
    private boolean isPasswordVisible = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminlogin);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

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
            String adminEmail = email.getText().toString().trim();
            String adminPassword = password.getText().toString().trim();

            if (TextUtils.isEmpty(adminEmail) || TextUtils.isEmpty(adminPassword)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            
            // Check against hardcoded credentials
            if (adminEmail.equals(ADMIN_EMAIL) && adminPassword.equals(ADMIN_PASSWORD)) {
                // Save login state
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                // Navigate to admin dashboard
                Intent intent = new Intent(adminlogin.this, AdminDashboardActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(adminlogin.this, "Invalid admin credentials",
                        Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
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
} 