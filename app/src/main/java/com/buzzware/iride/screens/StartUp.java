package com.buzzware.iride.screens;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.buzzware.iride.R;
import com.buzzware.iride.databinding.ActivityStartupBinding;

public class StartUp extends BaseActivity {

    ActivityStartupBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_startup);

    }

    public void Continue(View view) {

        if (getUserId() != null && !getUserId().isEmpty()) {

            startActivity(new Intent(this, BookARideActivity.class));

        } else {

            startActivity(new Intent(this, Authentication.class));
        }
    }
}