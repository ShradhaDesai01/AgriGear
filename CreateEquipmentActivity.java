package com.example.agrigear;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateEquipmentActivity extends AppCompatActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private LinearLayout imagesContainer;
    private Button btnPickImages, btnSubmit;
    private EditText etName, etSpec, etCostHour, etCostDay, etModel, etLandmark, etAddress, etPincode, etMobile;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_equipment);

        etName = findViewById(R.id.etName);
        etSpec = findViewById(R.id.etSpec);
        etCostHour = findViewById(R.id.etCostHour);
        etCostDay = findViewById(R.id.etCostDay);
        etModel = findViewById(R.id.etModel);
        etLandmark = findViewById(R.id.etLandmark);
        etAddress = findViewById(R.id.etAddress);
        etPincode = findViewById(R.id.etPincode);
        etMobile = findViewById(R.id.etMobile);
        imagesContainer = findViewById(R.id.imagesContainer);
        btnPickImages = findViewById(R.id.btnPickImages);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnPickImages.setOnClickListener(v -> pickImages());
        btnSubmit.setOnClickListener(v -> submitEquipment());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("equipments");
        mStorage = FirebaseStorage.getInstance().getReference("equipment_images");
    }

    private void pickImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                        addImageView(imageUri);
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                    addImageView(imageUri);
                }
            }
        }
    }

    private void addImageView(Uri imageUri) {
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(imageUri);
        int size = (int) getResources().getDimension(android.R.dimen.app_icon_size);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        imagesContainer.addView(imageView);
    }

    private void submitEquipment() {
        String name = etName.getText().toString().trim();
        String spec = etSpec.getText().toString().trim();
        String costHour = etCostHour.getText().toString().trim();
        String costDay = etCostDay.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String landmark = etLandmark.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String pincode = etPincode.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();

        if (name.isEmpty() || spec.isEmpty() || costHour.isEmpty() || costDay.isEmpty() || model.isEmpty() || landmark.isEmpty() || address.isEmpty() || pincode.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUris.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageBase64 = uriToBase64(imageUris.get(0));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vendors").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String uniqueId = documentSnapshot.getString("uniqueId");
                if (uniqueId != null) {
                    Map<String, Object> equipmentData = new HashMap<>();
                    equipmentData.put("name", name);
                    equipmentData.put("specification", spec);
                    equipmentData.put("costPerHour", costHour);
                    equipmentData.put("costPerDay", costDay);
                    equipmentData.put("model", model);
                    equipmentData.put("landmark", landmark);
                    equipmentData.put("address", address);
                    equipmentData.put("pincode", pincode);
                    equipmentData.put("mobile", mobile);
                    equipmentData.put("imageBase64", imageBase64);
                    equipmentData.put("approved", false);
                    equipmentData.put("uniqueId", uniqueId);

                    db.collection("equipments")
                        .add(equipmentData)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Equipment added successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to save equipment: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                } else {
                    Toast.makeText(this, "Your profile is missing a unique ID. Cannot add equipment.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "User profile not found.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String uriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // 50% quality to reduce size
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 