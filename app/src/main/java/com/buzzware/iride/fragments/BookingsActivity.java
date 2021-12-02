package com.buzzware.iride.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iride.adapters.BookingPagerAdapter;
import com.buzzware.iride.R;
import com.buzzware.iride.databinding.FragmentBookingsBinding;
import com.buzzware.iride.screens.BaseNavDrawer;

public class BookingsActivity extends BaseNavDrawer implements View.OnClickListener {

    FragmentBookingsBinding mBinding;

    BookingPagerAdapter bookingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = FragmentBookingsBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        Init();

        mBinding.drawerIcon.setOnClickListener(v -> OpenCloseDrawer());

        setBaseListeners();
//        setListeners();
    }


    private void Init() {
        bookingPagerAdapter = new BookingPagerAdapter(getSupportFragmentManager(), 2);
        mBinding.viewPager.setAdapter(bookingPagerAdapter);
        mBinding.viewPager.setCurrentItem(0);
        SetupTabView(0);
        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                SetupTabView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        ////init click
        mBinding.firstTabLay.setOnClickListener(v->SetupTabView( 0));
        mBinding.secondTabLay.setOnClickListener(v->SetupTabView( 1));
    }

    public void SetupTabView(int position) {
        if (position == 0) //history tab
        {
            mBinding.seconfTabTv.setTextColor(getResources().getColor(R.color.gray_light));
            mBinding.seccondTabLine.setBackgroundColor(getResources().getColor(R.color.white));
            mBinding.firstTabTv.setTextColor(getResources().getColor(R.color.black));
            mBinding.firstTabLine.setBackgroundColor(getResources().getColor(R.color.purple_200));
        } else { /// upcomming tab
            mBinding.firstTabTv.setTextColor(getResources().getColor(R.color.gray_light));
            mBinding.firstTabLine.setBackgroundColor(getResources().getColor(R.color.white));
            mBinding.seconfTabTv.setTextColor(getResources().getColor(R.color.black));
            mBinding.seccondTabLine.setBackgroundColor(getResources().getColor(R.color.purple_200));
        }
    }
}