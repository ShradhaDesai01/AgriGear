package com.example.agrigear;

import android.app.AlertDialog;
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

public class ViewVendorDetailsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VendorAdapter adapter;
    private List<Vendor> vendorList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vendor_details);

        recyclerView = findViewById(R.id.recyclerViewVendors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VendorAdapter(vendorList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadAllVendors();
    }

    private void loadAllVendors() {
        db.collection("vendors")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                vendorList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Vendor vendor = document.toObject(Vendor.class);
                    vendorList.add(vendor);
                }
                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(ViewVendorDetailsActivity.this, "Failed to load vendors.", Toast.LENGTH_SHORT).show();
            });
    }

    private class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {
        private List<Vendor> vendorList;
        public VendorAdapter(List<Vendor> vendorList) {
            this.vendorList = vendorList;
        }
        @NonNull
        @Override
        public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor_detail, parent, false);
            return new VendorViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
            Vendor vendor = vendorList.get(position);
            holder.bind(vendor);
        }
        @Override
        public int getItemCount() {
            return vendorList.size();
        }
        class VendorViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail, tvMobile, tvCity, tvAddress, tvUniqueId;
            Button btnDeleteVendor;
            public VendorViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvVendorName);
                tvEmail = itemView.findViewById(R.id.tvVendorEmail);
                tvMobile = itemView.findViewById(R.id.tvVendorMobile);
                tvCity = itemView.findViewById(R.id.tvVendorCity);
                tvAddress = itemView.findViewById(R.id.tvVendorAddress);
                tvUniqueId = itemView.findViewById(R.id.tvVendorUniqueId);
                btnDeleteVendor = itemView.findViewById(R.id.btnDeleteVendor);
            }
            public void bind(Vendor vendor) {
                tvName.setText("Name: " + vendor.getName());
                tvEmail.setText("Email: " + vendor.getEmail());
                tvMobile.setText("Mobile: " + vendor.getMobile());
                tvCity.setText("City: " + vendor.getCity());
                tvAddress.setText("Address: " + vendor.getAddress());
                tvUniqueId.setText("Unique ID: " + vendor.getUniqueId());
                btnDeleteVendor.setOnClickListener(v -> showDeleteConfirmation(vendor));
            }
        }
    }

    private void showDeleteConfirmation(Vendor vendor) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Vendor")
            .setMessage("Are you sure you want to delete this vendor?")
            .setPositiveButton("Delete", (dialog, which) -> deleteVendor(vendor))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteVendor(Vendor vendor) {
        // Find the vendor's document by uniqueId
        db.collection("vendors")
            .whereEqualTo("uniqueId", vendor.getUniqueId())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    db.collection("vendors").document(document.getId()).delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Vendor deleted", Toast.LENGTH_SHORT).show();
                            loadAllVendors();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete vendor", Toast.LENGTH_SHORT).show());
                }
            });
    }
} 