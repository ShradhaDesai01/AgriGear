package com.example.agrigear;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class ViewUserDetailsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_details);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(userList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadAllUsers();
    }

    private void loadAllUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                userList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    User user = document.toObject(User.class);
                    userList.add(user);
                }
                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(ViewUserDetailsActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
            });
    }

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private List<User> userList;
        public UserAdapter(List<User> userList) {
            this.userList = userList;
        }
        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_user_detail, parent, false);
            return new UserViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = userList.get(position);
            holder.bind(user);
        }
        @Override
        public int getItemCount() {
            return userList.size();
        }
        class UserViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail, tvMobile, tvCity, tvAddress, tvUserUniqueId;
            Button btnDeleteUser;
            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvUserName);
                tvEmail = itemView.findViewById(R.id.tvUserEmail);
                tvMobile = itemView.findViewById(R.id.tvUserMobile);
                tvCity = itemView.findViewById(R.id.tvUserCity);
                tvAddress = itemView.findViewById(R.id.tvUserAddress);
                tvUserUniqueId = itemView.findViewById(R.id.tvUserUniqueId);
                btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
            }
            public void bind(User user) {
                tvName.setText("Name: " + user.getName());
                tvEmail.setText("Email: " + user.getEmail());
                tvMobile.setText("Mobile: " + user.getMobile());
                tvCity.setText("City: " + user.getCity());
                tvAddress.setText("Address: " + user.getAddress());
                tvUserUniqueId.setText("Unique ID: " + user.getUniqueId());
                btnDeleteUser.setOnClickListener(v -> showDeleteConfirmation(user));
            }
        }
    }

    private void showDeleteConfirmation(User user) {
        new AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete this user?")
            .setPositiveButton("Delete", (dialog, which) -> deleteUser(user))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteUser(User user) {
        // Find the user's document by matching fields (email is unique)
        db.collection("users")
            .whereEqualTo("email", user.getEmail())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    db.collection("users").document(document.getId()).delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                            loadAllUsers();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show());
                }
            });
    }
} 