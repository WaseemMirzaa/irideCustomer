package com.buzzware.iride.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.buzzware.iride.screens.UpcommingFragment;

public class BookingPagerAdapter extends FragmentPagerAdapter {
    int tabCount;
    public BookingPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabCount= behavior;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new UpcommingFragment();
                break;
            case 1:
                fragment = new UpcommingFragment();
                break;
        }
        return fragment;
    }
    @Override
    public int getCount() {
        return tabCount;
    }
}
