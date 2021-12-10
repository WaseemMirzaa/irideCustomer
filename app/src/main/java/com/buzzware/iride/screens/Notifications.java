package com.buzzware.iride.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.buzzware.iride.R;
import com.buzzware.iride.databinding.ActivityNotificationsBinding;

public class Notifications extends AppCompatActivity {

    ActivityNotificationsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setListeners();



    }

    private void setListeners() {

        binding.drawerIcon.setOnClickListener(v -> onBackPressed());

    }

}