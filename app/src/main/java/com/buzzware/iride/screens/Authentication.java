package com.buzzware.iride.screens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.buzzware.iride.adapters.AuthPagerAdapter;
import com.buzzware.iride.R;
import com.buzzware.iride.databinding.ActivityAuthenticationBinding;
import com.google.android.material.tabs.TabLayout;

public class Authentication extends AppCompatActivity {

    ActivityAuthenticationBinding mBinding;
    AuthPagerAdapter authPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_authentication);
        try{
            Init();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void Init() {
        InitViewPager();
    }

    private void InitViewPager() {
        authPagerAdapter = new AuthPagerAdapter(getSupportFragmentManager(), mBinding.tabLay.getTabCount());
        mBinding.viewPager.setAdapter(authPagerAdapter);
        mBinding.tabLay.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBinding.viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0 || tab.getPosition() == 1)
                    authPagerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //scroll listener
        mBinding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mBinding.tabLay));
    }
}