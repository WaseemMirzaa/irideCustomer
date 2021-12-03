package com.buzzware.iride.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iride.adapters.BookingPagerAdapter;
import com.buzzware.iride.R;
import com.buzzware.iride.adapters.RideType;
import com.buzzware.iride.adapters.UpcomingRidesAdapter;
import com.buzzware.iride.databinding.FragmentBookingsBinding;
import com.buzzware.iride.models.RideModel;
import com.buzzware.iride.screens.BaseNavDrawer;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class BookingsActivity extends BaseNavDrawer implements View.OnClickListener {

    FragmentBookingsBinding mBinding;

//    BookingPagerAdapter bookingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = FragmentBookingsBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        Init();

        mBinding.drawerIcon.setOnClickListener(v -> OpenCloseDrawer());

        setBaseListeners();

        SetupTabView(0);
//        setListeners();
    }


    private void Init() {

        mBinding.firstTabLay.setOnClickListener(v -> SetupTabView(0));
        mBinding.secondTabLay.setOnClickListener(v -> SetupTabView(1));
    }

    public void SetupTabView(int position) {
        if (position == 0) //history tab
        {
            mBinding.seconfTabTv.setTextColor(getResources().getColor(R.color.gray_light));
            mBinding.seccondTabLine.setBackgroundColor(getResources().getColor(R.color.white));
            mBinding.firstTabTv.setTextColor(getResources().getColor(R.color.black));
            mBinding.firstTabLine.setBackgroundColor(getResources().getColor(R.color.purple_200));
            rideType = RideType.completed;

        } else { /// upcomming tab
            mBinding.firstTabTv.setTextColor(getResources().getColor(R.color.gray_light));
            mBinding.firstTabLine.setBackgroundColor(getResources().getColor(R.color.white));
            mBinding.seconfTabTv.setTextColor(getResources().getColor(R.color.black));
            mBinding.seccondTabLine.setBackgroundColor(getResources().getColor(R.color.purple_200));

            rideType = RideType.upcoming;


        }

        rides = new ArrayList<>();

        setAdapter(rides);

        showLoader();
        getRides();

    }

    RideType rideType = RideType.completed;

    private void getRides() {

        Query query;

        if (rideType != RideType.completed) {

            query = FirebaseFirestore.getInstance().collection("Bookings")
                    .whereEqualTo("userId", getUserId())
                    .whereIn("status", Arrays.asList("booked", "driverAccepted", "driverReached", "rideStarted"));

        } else {

            query = FirebaseFirestore.getInstance().collection("Bookings")
                    .whereEqualTo("userId", getUserId())
                    .whereIn("status", Arrays.asList("rated", "rideCompleted"));

        }
        query.get()
                .addOnCompleteListener(
                        this::parseSnapshot
                );

    }

    ArrayList<RideModel> rides;

    void parseSnapshot(Task<QuerySnapshot> task) {

        hideLoader();

        rides = new ArrayList<>();

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                RideModel rideModel = document.toObject(RideModel.class);

                rideModel.id = document.getId();

                rides.add(rideModel);

            }

        }

        setAdapter(rides);

    }

    private void setAdapter(ArrayList<RideModel> rides) {

        mBinding.rv.setLayoutManager(new LinearLayoutManager(BookingsActivity.this));

        mBinding.rv.setAdapter(new UpcomingRidesAdapter(BookingsActivity.this,
                rides,
                new UpcomingRidesAdapter.UpcomingRideActionListener() {
                    @Override
                    public void acceptRide(RideModel rideModel) {

//                        accept(rideModel);

                    }

                    @Override
                    public void moveToCompleteScreen(RideModel rideModel) {

//                        moveToOnTrip(rideModel);

                    }
                },
                RideType.completed));

    }
}