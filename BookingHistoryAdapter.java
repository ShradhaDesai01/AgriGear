package com.example.agrigear;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {
    private List<Booking> bookingList;

    public BookingHistoryAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.tvEquipmentName.setText(booking.getEquipmentName());
        holder.tvBookingDate.setText("Booking Date: " + booking.getDate());
        holder.tvBookingStatus.setText("Status: " + booking.getStatus());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEquipmentName, tvBookingDate, tvBookingStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEquipmentName = itemView.findViewById(R.id.tvEquipmentName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
        }
    }
} 