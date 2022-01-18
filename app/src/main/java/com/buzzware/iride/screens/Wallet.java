package com.buzzware.iride.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iride.Firebase.FirebaseInstances;
import com.buzzware.iride.adapters.PaymentsAdapter;
import com.buzzware.iride.databinding.FragmentWalletBinding;
import com.buzzware.iride.models.ScheduleModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class Wallet extends BaseNavDrawer implements View.OnClickListener {

    FragmentWalletBinding mBinding;

    List<ScheduleModel> ridesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = FragmentWalletBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        initView();

        getSchedulesListFromFirebase();

        setListeners();

    }

    private void initView() {

        mBinding.menuAppBar.menuAppBarTitle.setText("Payments");

    }

    private void setListeners() {

        mBinding.myCardsBt.setOnClickListener(v -> {

            startActivity(new Intent(Wallet.this, MyCards.class));

        });

        mBinding.menuAppBar.drawerIcon.setOnClickListener(v -> openCloseDrawer());

    }

    private void getSchedulesListFromFirebase() {

        FirebaseInstances.scheduledRidesCollection
                .whereEqualTo("userId",getUserId())
                .addSnapshotListener((value, error) -> {

            if (error != null && error.getLocalizedMessage() != null)

                showErrorAlert(error.getLocalizedMessage());

            else
            {

                parseScheduledRidesSnapshot(value);

            }

        });
    }

    private void parseScheduledRidesSnapshot(QuerySnapshot value) {

        if(value == null || value.getDocuments() == null)

            return;

        List<DocumentSnapshot> snapshots = value.getDocuments();

        ridesList = new ArrayList<>();

        for(DocumentSnapshot ride: snapshots) {

            ScheduleModel schedule = ride.toObject(ScheduleModel.class);

            schedule.id = ride.getId();

            schedule.status = "Scheduled";

            ridesList.add(schedule);
        }

        getBookingsFromFirebase();

    }


    private void getBookingsFromFirebase() {

        FirebaseInstances.bookingsCollection.addSnapshotListener((value, error) -> {

            if (error != null && error.getLocalizedMessage() != null)

                showErrorAlert(error.getLocalizedMessage());

            else
            {

                parseBookingsSnapshot(value);

            }

        });
    }

    private void parseBookingsSnapshot(QuerySnapshot value) {

        if(value == null || value.getDocuments() == null)

            return;

        List<DocumentSnapshot> snapshots = value.getDocuments();

        for(DocumentSnapshot ride: snapshots) {

            ScheduleModel schedule = ride.toObject(ScheduleModel.class);

            schedule.id = ride.getId();

            ridesList.add(schedule);
        }

        setAdapter(ridesList);

    }

    private void setAdapter(List<ScheduleModel> ridesList) {

        mBinding.paymentsRV.setLayoutManager(new LinearLayoutManager(this));

        mBinding.paymentsRV.setAdapter(new PaymentsAdapter(this, ridesList));

    }

}