package com.buzzware.iride.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iride.R;
import com.buzzware.iride.databinding.FragmentCustomerServiceBinding;
import com.buzzware.iride.screens.BaseNavDrawer;

public class CustomerService extends BaseNavDrawer {

    FragmentCustomerServiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentCustomerServiceBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.drawerIcon.setOnClickListener(v -> OpenCloseDrawer());

    }

}