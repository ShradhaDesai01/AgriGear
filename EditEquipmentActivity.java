package com.example.agrigear;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditEquipmentActivity extends AppCompatActivity {
    private EditText etName, etSpec, etCostHour, etCostDay, etModel, etAddress, etLandmark;
    private Button btnSave;
    private DatabaseReference mDatabase;
    private String equipmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_equipment);

        etName = findViewById(R.id.etName);
        etSpec = findViewById(R.id.etSpec);
        etCostHour = findViewById(R.id.etCostHour);
        etCostDay = findViewById(R.id.etCostDay);
        etModel = findViewById(R.id.etModel);
        etAddress = findViewById(R.id.etAddress);
        etLandmark = findViewById(R.id.etLandmark);
        btnSave = findViewById(R.id.btnSave);

        equipmentId = getIntent().getStringExtra("equipment_id");
        mDatabase = FirebaseDatabase.getInstance().getReference("equipment");

        loadEquipmentDetails();

        btnSave.setOnClickListener(v -> saveEquipment());
    }

    private void loadEquipmentDetails() {
        if (TextUtils.isEmpty(equipmentId)) {
            Toast.makeText(this, "Invalid equipment ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mDatabase.child(equipmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Equipment equipment = snapshot.getValue(Equipment.class);
                if (equipment != null) {
                    etName.setText(equipment.getName());
                    etSpec.setText(equipment.getSpecification());
                    etCostHour.setText(String.valueOf(equipment.getCostPerHour()));
                    etCostDay.setText(String.valueOf(equipment.getCostPerDay()));
                    etModel.setText(equipment.getModel());
                    etAddress.setText(equipment.getAddress());
                    etLandmark.setText(equipment.getLandmark());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditEquipmentActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveEquipment() {
        String name = etName.getText().toString().trim();
        String spec = etSpec.getText().toString().trim();
        String costHour = etCostHour.getText().toString().trim();
        String costDay = etCostDay.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String landmark = etLandmark.getText().toString().trim();

        if (name.isEmpty() || spec.isEmpty() || costHour.isEmpty() || costDay.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child(equipmentId).child("name").setValue(name);
        mDatabase.child(equipmentId).child("specification").setValue(spec);
        mDatabase.child(equipmentId).child("costPerHour").setValue(costHour);
        mDatabase.child(equipmentId).child("costPerDay").setValue(costDay);
        mDatabase.child(equipmentId).child("model").setValue(model);
        mDatabase.child(equipmentId).child("address").setValue(address);
        mDatabase.child(equipmentId).child("landmark").setValue(landmark)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Equipment updated", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }
} 