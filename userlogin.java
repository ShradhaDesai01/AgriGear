package com.example.agrigear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.view.MotionEvent;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class userlogin extends AppCompatActivity {

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

    private static final String PREFS_NAME = "UserPrefs";
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlogin);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Initialize views
        initializeViews();

        // Load saved credentials
        loadRememberedCredentials();

        // Set up click listeners
        setupClickListeners();

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
    }

    private void initializeViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        registerLink = findViewById(R.id.registerLink);
        txtForgot = findViewById(R.id.txtForgot);
        checkRemember = findViewById(R.id.checkRemember);
        progressBar = findViewById(R.id.progressBar);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                performLogin();
            }
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, UserRegisterActivity.class));
        });

        txtForgot.setOnClickListener(v -> {
            handleForgotPassword();
        });
    }

    private boolean validateInputs() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performLogin() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this, task -> {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                if (task.isSuccessful()) {
                    String userId = mAuth.getCurrentUser().getUid();
                    checkUserRegistration(userId);
                } else {
                    String errorMessage = task.getException().getMessage();
                    if (errorMessage != null && errorMessage.contains("no user record")) {
                        Toast.makeText(userlogin.this, 
                            "No account found. Please register first.", 
                            Toast.LENGTH_LONG).show();
                        startActivity(new Intent(userlogin.this, UserRegisterActivity.class));
                    } else {
                        Toast.makeText(userlogin.this, 
                            "Authentication failed: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void handleForgotPassword() {
        String userEmail = email.getText().toString().trim();
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        mAuth.sendPasswordResetEmail(userEmail)
            .addOnCompleteListener(task -> {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                if (task.isSuccessful()) {
                    Toast.makeText(userlogin.this, 
                        "Password reset email sent. Please check your inbox.",
                        Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(userlogin.this, 
                        "Failed to send reset email: " + task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void checkUserRegistration(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (documentSnapshot.exists()) {
                if (checkRemember.isChecked()) {
                    editor.putString("email", email.getText().toString().trim());
                    editor.putString("password", password.getText().toString().trim());
                    editor.putBoolean("remember", true);
                    editor.apply();
                } else {
                    editor.clear().apply();
                }
                Intent intent = new Intent(userlogin.this, UserDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(userlogin.this,
                        "Account not properly registered. Please register again.",
                        Toast.LENGTH_LONG).show();
                mAuth.signOut();
                startActivity(new Intent(userlogin.this, UserRegisterActivity.class));
            }
        }).addOnFailureListener(e -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            Toast.makeText(userlogin.this, "Failed to check registration: " + e.getMessage(), Toast.LENGTH_LONG).show();
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