package com.buzzware.iride.screens;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iride.adapters.ScheduledRidesAdapter;
import com.buzzware.iride.databinding.FragmentBookingsBinding;
import com.buzzware.iride.models.ScheduleModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ScheduledRides extends BaseNavDrawer implements View.OnClickListener {

    FragmentBookingsBinding mBinding;

//    BookingPagerAdapter bookingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = FragmentBookingsBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        Init();

        mBinding.drawerIcon.setOnClickListener(v -> openCloseDrawer());

        setBaseListeners();

        setAdapter(new ArrayList<>());

        showLoader();

        getRides();

//        setListeners();
    }


    private void Init() {
    
        mBinding.customTabLay.setVisibility(View.GONE);
    
        mBinding.tvTitle.setText("Scheduled Rides");
    }

    private void getRides() {

        Query query;
        
            query = FirebaseFirestore.getInstance().collection("ScheduledRides")
                    .whereEqualTo("userId", getUserId());
       
        query.get()
                .addOnCompleteListener(
                        this::parseSnapshot
                );

    }

    ArrayList<ScheduleModel> rides;

    void parseSnapshot(Task<QuerySnapshot> task) {

        hideLoader();

        rides = new ArrayList<>();

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                ScheduleModel scheduleModel = document.toObject(ScheduleModel.class);

                scheduleModel.id = document.getId();

                rides.add(scheduleModel);

            }

        }

        setAdapter(rides);

    }

    private void setAdapter(ArrayList<ScheduleModel> rides) {

        mBinding.rv.setLayoutManager(new LinearLayoutManager(ScheduledRides.this));

        mBinding.rv.setAdapter(new ScheduledRidesAdapter(ScheduledRides.this,
                rides));

    }
}