package com.buzzware.iride.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.iride.databinding.FragmentInvitationBinding;

public class Invitation extends BaseNavDrawer {

    FragmentInvitationBinding binding;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = FragmentInvitationBinding.inflate(getLayoutInflater());

        binding.btnContinue.setOnClickListener(v -> shareIRide());

        setContentView(binding.getRoot());

        binding.drawerIcon.setOnClickListener(v -> openCloseDrawer());
    }

    void shareIRide() {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.buzzware.iride");
        startActivity(Intent.createChooser(i, "Share URL"));
    }

}