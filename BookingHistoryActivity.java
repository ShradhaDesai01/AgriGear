package com.example.agrigear;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private BookingHistoryAdapter adapter;
    private List<Booking> bookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Booking History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewBookingHistory);
        bookingList = new ArrayList<>();
        adapter = new BookingHistoryAdapter(bookingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadUserBookingHistory();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadUserBookingHistory() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "unknown_user";
        db.collection("bookings")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                bookingList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Booking booking = document.toObject(Booking.class);
                    if (booking != null) {
                        booking.setId(document.getId());
                        bookingList.add(booking);
                    }
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Fetched " + bookingList.size() + " bookings", Toast.LENGTH_SHORT).show();
                if (bookingList.isEmpty()) {
                    Toast.makeText(this, "No booking history found", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load booking history: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }
} 