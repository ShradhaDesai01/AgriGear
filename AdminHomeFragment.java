package com.example.agrigear;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class AdminHomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        MaterialButton btnApproveVendors = view.findViewById(R.id.btnApproveVendors);
        MaterialButton btnViewUserDetails = view.findViewById(R.id.btnViewUserDetails);

        btnApproveVendors.setOnClickListener(v -> {
            // TODO: Replace with navigation to ApproveVendorsFragment
        });
        btnViewUserDetails.setOnClickListener(v -> {
            // TODO: Replace with navigation to ViewUserDetailsFragment
        });
        return view;
    }
} 