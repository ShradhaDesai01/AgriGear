package com.example.agrigear;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class UpdateEquipmentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EquipmentAdapter adapter;
    private List<Equipment> equipmentList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_equipment);

        recyclerView = findViewById(R.id.recyclerViewEquipment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EquipmentAdapter(equipmentList, new OnEquipmentActionListener() {
            @Override
            public void onEdit(Equipment equipment) {
                Intent intent = new Intent(UpdateEquipmentActivity.this, EditEquipmentActivity.class);
                intent.putExtra("equipment_id", equipment.getId());
                startActivity(intent);
            }
            @Override
            public void onDelete(Equipment equipment) {
                showDeleteConfirmation(equipment);
            }
        });
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("vendors").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String uniqueId = documentSnapshot.getString("uniqueId");
                if (uniqueId != null) {
                    loadVendorEquipmentByUniqueId(uniqueId);
                }
            }
        });
    }

    private void loadVendorEquipmentByUniqueId(String uniqueId) {
        db.collection("equipments")
            .whereEqualTo("uniqueId", uniqueId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                equipmentList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Equipment equipment = document.toObject(Equipment.class);
                    equipment.setId(document.getId());
                    equipmentList.add(equipment);
                }
                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(UpdateEquipmentActivity.this, "Failed to load equipment.", Toast.LENGTH_SHORT).show();
            });
    }

    private void showDeleteConfirmation(Equipment equipment) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Equipment")
            .setMessage("Are you sure you want to delete this equipment?")
            .setPositiveButton("Delete", (dialog, which) -> deleteEquipment(equipment))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteEquipment(Equipment equipment) {
        db.collection("equipments").document(equipment.getId())
            .delete()
            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Equipment deleted", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show());
    }

    private class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {
        private List<Equipment> equipmentList;
        private OnEquipmentActionListener listener;

        public EquipmentAdapter(List<Equipment> equipmentList, OnEquipmentActionListener listener) {
            this.equipmentList = equipmentList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_equipment_update, parent, false);
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
            private TextView nameText, specText, costText;
            private Button btnEdit, btnDelete;

            public EquipmentViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.tvEquipmentName);
                specText = itemView.findViewById(R.id.tvEquipmentSpec);
                costText = itemView.findViewById(R.id.tvEquipmentCost);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }

            public void bind(Equipment equipment) {
                nameText.setText(equipment.getName());
                specText.setText(equipment.getSpecification());
                costText.setText("₹" + equipment.getCostPerHour() + "/hr, ₹" + equipment.getCostPerDay() + "/day");
                btnEdit.setOnClickListener(v -> listener.onEdit(equipment));
                btnDelete.setOnClickListener(v -> listener.onDelete(equipment));
            }
        }
    }

    public interface OnEquipmentActionListener {
        void onEdit(Equipment equipment);
        void onDelete(Equipment equipment);
    }
} 