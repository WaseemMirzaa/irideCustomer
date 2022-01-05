package com.buzzware.iride.screens;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.iride.databinding.ActivityAboutBinding;

public class AboutUs extends BaseActivity {

    ActivityAboutBinding binding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAboutBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.include.drawerIcon.setOnClickListener(v -> onBackPressed());
    }
}
