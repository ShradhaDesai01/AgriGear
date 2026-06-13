package com.example.agrigear;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class VendorBookingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_booking);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Booking Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadBookings();
    }

    private void loadBookings() {
        db.collection("bookings")
            .whereEqualTo("status", "pending")
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
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load bookings: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void acceptBooking(String bookingId) {
        db.collection("bookings").document(bookingId)
            .update("status", "approved")
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Booking approved successfully!", Toast.LENGTH_SHORT).show();
                loadBookings(); // Reload the list
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to approve booking: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void rejectBooking(String bookingId) {
        db.collection("bookings").document(bookingId)
            .update("status", "rejected")
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Booking rejected!", Toast.LENGTH_SHORT).show();
                loadBookings(); // Reload the list
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to reject booking: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_request, parent, false);
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
            private TextView equipmentNameText, dateText, timeText, durationText, purposeText, costText;
            private Button acceptButton, rejectButton;

            public BookingViewHolder(@NonNull View itemView) {
                super(itemView);
                equipmentNameText = itemView.findViewById(R.id.equipmentName);
                dateText = itemView.findViewById(R.id.dateText);
                timeText = itemView.findViewById(R.id.timeText);
                durationText = itemView.findViewById(R.id.durationText);
                purposeText = itemView.findViewById(R.id.purposeText);
                costText = itemView.findViewById(R.id.costText);
                acceptButton = itemView.findViewById(R.id.btnAccept);
                rejectButton = itemView.findViewById(R.id.btnReject);
            }

            public void bind(Booking booking) {
                equipmentNameText.setText("Equipment: " + booking.getEquipmentName());
                dateText.setText("Date: " + booking.getDate());
                timeText.setText("Time: " + booking.getTime());
                durationText.setText("Duration: " + booking.getDuration() + " hours");
                purposeText.setText("Purpose: " + booking.getPurpose());
                costText.setText("Total Cost: ₹" + booking.getTotalCost());

                acceptButton.setOnClickListener(v -> acceptBooking(booking.getId()));
                rejectButton.setOnClickListener(v -> rejectBooking(booking.getId()));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 