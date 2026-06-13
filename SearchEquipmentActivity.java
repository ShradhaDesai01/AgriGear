package com.example.agrigear;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.util.ArrayList;
import java.util.List;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchEquipmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EquipmentAdapter adapter;
    private List<Equipment> equipmentList;
    private FirebaseFirestore db;
    private EditText searchEditText;
    private List<Equipment> filteredEquipmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_equipment);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Available Equipment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        equipmentList = new ArrayList<>();
        filteredEquipmentList = new ArrayList<>();
        adapter = new EquipmentAdapter(filteredEquipmentList);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEquipmentList(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        loadApprovedEquipment();
    }

    private void loadApprovedEquipment() {
        db.collection("equipments")
            .whereEqualTo("approved", true)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                equipmentList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Equipment equipment = document.toObject(Equipment.class);
                    if (equipment != null) {
                        equipment.setId(document.getId());
                        equipmentList.add(equipment);
                    }
                }
                filterEquipmentList(searchEditText.getText().toString());
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load equipment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void filterEquipmentList(String query) {
        filteredEquipmentList.clear();
        if (query.isEmpty()) {
            filteredEquipmentList.addAll(equipmentList);
        } else {
            for (Equipment equipment : equipmentList) {
                if (equipment.getName() != null && equipment.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredEquipmentList.add(equipment);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void bookEquipment(Equipment equipment) {
        Intent intent = new Intent(this, EquipmentBookingActivity.class);
        intent.putExtra("equipment_id", equipment.getId());
        intent.putExtra("equipment_name", equipment.getName());
        intent.putExtra("equipment_cost_hour", equipment.getCostPerHour());
        intent.putExtra("equipment_cost_day", equipment.getCostPerDay());
        intent.putExtra("vendor_mobile", equipment.getMobile());
        startActivity(intent);
    }

    private class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {
        private List<Equipment> equipmentList;

        public EquipmentAdapter(List<Equipment> equipmentList) {
            this.equipmentList = equipmentList;
        }

        @NonNull
        @Override
        public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_equipment_search, parent, false);
            return new EquipmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position) {
            Equipment equipment = equipmentList.get(position);
            holder.bind(equipment);
        }

        @Override
        public int getItemCount() {
            return equipmentList.size();
        }

        class EquipmentViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;
            private TextView nameText, specText, costText, modelText, addressText, vendorNameText, vendorContactText, vendorUniqueIdText;
            private Button bookButton;

            public EquipmentViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.equipmentImage);
                nameText = itemView.findViewById(R.id.equipmentName);
                specText = itemView.findViewById(R.id.equipmentSpec);
                costText = itemView.findViewById(R.id.equipmentCost);
                modelText = itemView.findViewById(R.id.equipmentModel);
                addressText = itemView.findViewById(R.id.equipmentAddress);
                vendorNameText = itemView.findViewById(R.id.vendorName);
                vendorContactText = itemView.findViewById(R.id.vendorContact);
                vendorUniqueIdText = itemView.findViewById(R.id.vendorUniqueId);
                bookButton = itemView.findViewById(R.id.btnBook);
            }

            public void bind(Equipment equipment) {
                nameText.setText("Name: " + equipment.getName());
                specText.setText("Specification: " + equipment.getSpecification());
                costText.setText("Cost: ₹" + equipment.getCostPerHour() + "/hr, ₹" + equipment.getCostPerDay() + "/day");
                modelText.setText("Model: " + equipment.getModel());
                addressText.setText("Address: " + equipment.getAddress() + ", " + equipment.getLandmark());
                vendorContactText.setText("Contact: ..."); // Placeholder while loading
                vendorNameText.setText("Vendor: ..."); // Placeholder while loading
                vendorUniqueIdText.setText("Unique ID: ..."); // Placeholder while loading

                // Fetch vendor details using uniqueId
                String uniqueId = equipment.getUniqueId();
                if (uniqueId != null && !uniqueId.isEmpty()) {
                    FirebaseFirestore.getInstance().collection("vendors")
                        .whereEqualTo("uniqueId", uniqueId)
                        .limit(1)
                        .get()
                        .addOnSuccessListener((QuerySnapshot snapshot) -> {
                            if (!snapshot.isEmpty()) {
                                Vendor vendor = snapshot.getDocuments().get(0).toObject(Vendor.class);
                                if (vendor != null) {
                                    vendorNameText.setText("Vendor: " + vendor.getName());
                                    vendorContactText.setText("Contact: " + vendor.getMobile());
                                    vendorUniqueIdText.setText("Unique ID: " + vendor.getUniqueId());
                                } else {
                                    vendorNameText.setText("Vendor: N/A");
                                    vendorContactText.setText("Contact: N/A");
                                    vendorUniqueIdText.setText("Unique ID: N/A");
                                }
                            } else {
                                vendorNameText.setText("Vendor: N/A");
                                vendorContactText.setText("Contact: N/A");
                                vendorUniqueIdText.setText("Unique ID: N/A");
                            }
                        })
                        .addOnFailureListener(e -> {
                            vendorNameText.setText("Vendor: N/A");
                            vendorContactText.setText("Contact: N/A");
                            vendorUniqueIdText.setText("Unique ID: N/A");
                        });
                } else {
                    vendorNameText.setText("Vendor: N/A");
                    vendorContactText.setText("Contact: N/A");
                    vendorUniqueIdText.setText("Unique ID: N/A");
                }

                // Load image from Base64
                if (equipment.getImageBase64() != null && !equipment.getImageBase64().isEmpty()) {
                    try {
                        byte[] imageBytes = Base64.decode(equipment.getImageBase64(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        imageView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        imageView.setImageResource(R.drawable.ic_tractor);
                    }
                } else {
                    imageView.setImageResource(R.drawable.ic_tractor);
                }

                bookButton.setOnClickListener(v -> bookEquipment(equipment));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 