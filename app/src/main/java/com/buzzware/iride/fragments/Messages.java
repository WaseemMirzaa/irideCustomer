package com.buzzware.iride.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iride.R;
import com.buzzware.iride.databinding.FragmentMessagesBinding;
import com.buzzware.iride.screens.BaseNavDrawer;

public class Messages extends BaseNavDrawer {

    FragmentMessagesBinding mBinding;

    public Messages() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_messages);
    }

    private void Init() {
        mBinding.firstItem.findViewById(R.id.btnProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}