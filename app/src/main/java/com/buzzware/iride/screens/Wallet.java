package com.buzzware.iride.screens;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.buzzware.iride.R;
import com.buzzware.iride.databinding.AddCardDialogBinding;
import com.buzzware.iride.databinding.FragmentWalletBinding;
import com.buzzware.iride.screens.BaseNavDrawer;

public class Wallet extends BaseNavDrawer implements View.OnClickListener {

    FragmentWalletBinding mBinding;

    public Wallet() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = FragmentWalletBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        mBinding.btnAddPayment.setOnClickListener(this);

        mBinding.drawerIcon.setOnClickListener(v -> OpenCloseDrawer());

    }


    @Override
    public void onClick(View v) {

        super.onClick(v);

        if (v == mBinding.btnAddPayment) {
            ShowPaymentDialog();
        }
    }

    private void ShowPaymentDialog() {
        Dialog myDialog = new Dialog(this);
        AddCardDialogBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(myDialog.getContext()),
                R.layout.add_card_dialog,
                (ViewGroup) myDialog.findViewById(R.id.parent),
                false);
        myDialog.setContentView(mBinding.getRoot());
        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}