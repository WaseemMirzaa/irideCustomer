package com.buzzware.iride.screens;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.iride.databinding.ActivityAboutBinding;
import com.buzzware.iride.databinding.ActivityNotificationDetailBinding;

public class NotificationDetail extends BaseActivity {

    ActivityNotificationDetailBinding binding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotificationDetailBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.include.drawerIcon.setOnClickListener(v -> onBackPressed());

        getAndDisplayData();
    }

    private void getAndDisplayData() {

        if(getIntent().getExtras() != null) {

            if(getIntent().getStringExtra("title") != null) {

                binding.titleTV.setText(getIntent().getStringExtra("title"));

            }

            if(getIntent().getStringExtra("msg") != null) {

                binding.messageTV.setText(getIntent().getStringExtra("msg"));

            }

        }

    }

    public static void startNotificationDetail(Context c, String title, String msg) {

        c.startActivity(new Intent(c, NotificationDetail.class)
                .putExtra("title", title)
                .putExtra("msg", msg));

    }
}
