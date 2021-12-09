package com.buzzware.iride.screens;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.iride.databinding.FragmentInvitationBinding;

public class Invitation extends BaseNavDrawer {

    FragmentInvitationBinding binding;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = FragmentInvitationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.drawerIcon.setOnClickListener(v -> openCloseDrawer());
    }

}