package com.buzzware.iride.screens;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.iride.databinding.ActivityAboutBinding;
import com.buzzware.iride.databinding.ActivityCovidBinding;

public class Covid extends BaseActivity {

    ActivityCovidBinding binding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCovidBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.include.drawerIcon.setOnClickListener(v -> onBackPressed());
    }
}
