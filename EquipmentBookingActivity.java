package com.example.agrigear;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EquipmentBookingActivity extends AppCompatActivity {

    private TextView equipmentNameText, costText;
    private EditText dateEditText, timeEditText, durationEditText, purposeEditText;
    private Button bookButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    
    private String equipmentId, equipmentName, costHour, costDay, vendorMobile;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_booking);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book Equipment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();

        // Get data from intent
        equipmentId = getIntent().getStringExtra("equipment_id");
        equipmentName = getIntent().getStringExtra("equipment_name");
        costHour = getIntent().getStringExtra("equipment_cost_hour");
        costDay = getIntent().getStringExtra("equipment_cost_day");
        vendorMobile = getIntent().getStringExtra("vendor_mobile");

        // Initialize views
        equipmentNameText = findViewById(R.id.equipmentName);
        costText = findViewById(R.id.costText);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        durationEditText = findViewById(R.id.durationEditText);
        purposeEditText = findViewById(R.id.purposeEditText);
        bookButton = findViewById(R.id.bookButton);

        // Set equipment details
        equipmentNameText.setText("Equipment: " + equipmentName);
        costText.setText("Cost: ₹" + costHour + "/hr, ₹" + costDay + "/day");

        // Set up date picker
        dateEditText.setOnClickListener(v -> showDatePicker());
        
        // Set up time picker
        timeEditText.setOnClickListener(v -> showTimePicker());

        // Set up booking button
        bookButton.setOnClickListener(v -> submitBooking());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateTimeLabel();
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        );
        timePickerDialog.show();
    }

    private void updateDateLabel() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        dateEditText.setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeLabel() {
        String timeFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        timeEditText.setText(sdf.format(calendar.getTime()));
    }

    private void submitBooking() {
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String duration = durationEditText.getText().toString().trim();
        String purpose = purposeEditText.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || duration.isEmpty() || purpose.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total cost
        double totalCost = 0;
        try {
            int durationHours = Integer.parseInt(duration);
            totalCost = durationHours * Double.parseDouble(costHour);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid duration", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create booking data
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("equipmentId", equipmentId);
        bookingData.put("equipmentName", equipmentName);
        bookingData.put("userId", mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "unknown_user");
        bookingData.put("vendorMobile", vendorMobile);
        bookingData.put("date", date);
        bookingData.put("time", time);
        bookingData.put("duration", duration);
        bookingData.put("purpose", purpose);
        bookingData.put("totalCost", totalCost);
        bookingData.put("status", "pending"); // pending, approved, rejected
        bookingData.put("timestamp", System.currentTimeMillis());

        // Save to Firestore
        db.collection("bookings")
            .add(bookingData)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(this, "Booking request sent successfully!", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to send booking request: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 