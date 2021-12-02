package com.buzzware.iride.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iride.R;
import com.buzzware.iride.databinding.FragmentInvitationBinding;
import com.buzzware.iride.screens.BaseNavDrawer;

public class Invitation extends BaseNavDrawer {

    FragmentInvitationBinding binding;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = FragmentInvitationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.drawerIcon.setOnClickListener(v -> OpenCloseDrawer());
    }

}