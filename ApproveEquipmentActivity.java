package com.example.agrigear;

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

public class ApproveEquipmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EquipmentAdapter adapter;
    private List<Equipment> equipmentList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_equipment);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Approve Equipment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        equipmentList = new ArrayList<>();
        adapter = new EquipmentAdapter(equipmentList);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadEquipment();
    }

    private void loadEquipment() {
        db.collection("equipments")
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
                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load equipment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void approveEquipment(String equipmentId) {
        db.collection("equipments").document(equipmentId)
            .update("approved", true)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Equipment approved successfully!", Toast.LENGTH_SHORT).show();
                loadEquipment(); // Reload the list
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to approve equipment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void rejectEquipment(String equipmentId) {
        db.collection("equipments").document(equipmentId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Equipment rejected and removed!", Toast.LENGTH_SHORT).show();
                loadEquipment(); // Reload the list
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to reject equipment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {
        private List<Equipment> equipmentList;

        public EquipmentAdapter(List<Equipment> equipmentList) {
            this.equipmentList = equipmentList;
        }

        @NonNull
        @Override
        public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_equipment_approval, parent, false);
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
            private TextView nameText, specText, costText, modelText, addressText;
            private Button approveButton, rejectButton;

            public EquipmentViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.equipmentImage);
                nameText = itemView.findViewById(R.id.equipmentName);
                specText = itemView.findViewById(R.id.equipmentSpec);
                costText = itemView.findViewById(R.id.equipmentCost);
                modelText = itemView.findViewById(R.id.equipmentModel);
                addressText = itemView.findViewById(R.id.equipmentAddress);
                approveButton = itemView.findViewById(R.id.btnApprove);
                rejectButton = itemView.findViewById(R.id.btnReject);
            }

            public void bind(Equipment equipment) {
                nameText.setText("Name: " + equipment.getName());
                specText.setText("Specification: " + equipment.getSpecification());
                costText.setText("Cost: ₹" + equipment.getCostPerHour() + "/hr, ₹" + equipment.getCostPerDay() + "/day");
                modelText.setText("Model: " + equipment.getModel());
                addressText.setText("Address: " + equipment.getAddress() + ", " + equipment.getLandmark());

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

                approveButton.setOnClickListener(v -> approveEquipment(equipment.getId()));
                rejectButton.setOnClickListener(v -> rejectEquipment(equipment.getId()));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 