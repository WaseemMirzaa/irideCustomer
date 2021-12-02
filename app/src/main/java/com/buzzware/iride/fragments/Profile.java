package com.buzzware.iride.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.buzzware.iride.R;
import com.buzzware.iride.screens.BaseNavDrawer;
import com.buzzware.iride.screens.Notifications;
import com.buzzware.iride.screens.Settings;
import com.buzzware.iride.databinding.FragmentProfileBinding;

public class Profile extends BaseNavDrawer implements View.OnClickListener{

    FragmentProfileBinding mBinding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding= FragmentProfileBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        Init();

    }


    private void Init() {
        ///init click

        mBinding.drawerIcon.setOnClickListener(v -> OpenCloseDrawer());

        mBinding.btnNotifications.setOnClickListener(this);
        mBinding.btnSettings.setOnClickListener(this);
        mBinding.btnChat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        super.onClick(v);

        if(v == mBinding.btnNotifications)
        {
            startActivity(new Intent(context, Notifications.class));
        }else if(v == mBinding.btnSettings)
        {
            startActivity(new Intent(context, Settings.class));
        }else if(v == mBinding.btnChat)
        {
            startActivity(new Intent(Profile.this, Chat.class));
        }
    }
}