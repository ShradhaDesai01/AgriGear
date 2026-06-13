package com.example.agrigear;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MyBookingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Bookings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loadUserBookings();
    }

    private void loadUserBookings() {
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
                
                if (bookingList.isEmpty()) {
                    Toast.makeText(this, "No bookings found", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load bookings: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
        private List<Booking> bookingList;

        public BookingAdapter(List<Booking> bookingList) {
            this.bookingList = bookingList;
        }

        @NonNull
        @Override
        public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_booking, parent, false);
            return new BookingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
            Booking booking = bookingList.get(position);
            holder.bind(booking);
        }

        @Override
        public int getItemCount() {
            return bookingList.size();
        }

        class BookingViewHolder extends RecyclerView.ViewHolder {
            private TextView equipmentNameText, dateText, timeText, durationText, purposeText, costText, statusText;

            public BookingViewHolder(@NonNull View itemView) {
                super(itemView);
                equipmentNameText = itemView.findViewById(R.id.equipmentName);
                dateText = itemView.findViewById(R.id.dateText);
                timeText = itemView.findViewById(R.id.timeText);
                durationText = itemView.findViewById(R.id.durationText);
                purposeText = itemView.findViewById(R.id.purposeText);
                costText = itemView.findViewById(R.id.costText);
                statusText = itemView.findViewById(R.id.statusText);
            }

            public void bind(Booking booking) {
                equipmentNameText.setText("Equipment: " + booking.getEquipmentName());
                dateText.setText("Date: " + booking.getDate());
                timeText.setText("Time: " + booking.getTime());
                durationText.setText("Duration: " + booking.getDuration() + " hours");
                purposeText.setText("Purpose: " + booking.getPurpose());
                costText.setText("Total Cost: ₹" + booking.getTotalCost());
                
                // Set status with color coding
                String status = booking.getStatus();
                statusText.setText("Status: " + status.toUpperCase());
                
                switch (status.toLowerCase()) {
                    case "pending":
                        statusText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                        break;
                    case "approved":
                        statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        break;
                    case "rejected":
                        statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        break;
                    default:
                        statusText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        break;
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 