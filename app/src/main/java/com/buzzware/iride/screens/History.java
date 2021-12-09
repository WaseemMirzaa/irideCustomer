package com.buzzware.iride.screens;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iride.adapters.HistoryAddapter;
import com.buzzware.iride.adapters.RideType;
import com.buzzware.iride.adapters.UpcomingRidesAdapter;
import com.buzzware.iride.databinding.FragmentHIstoryBinding;
import com.buzzware.iride.models.HistoryModel;
import com.buzzware.iride.models.RideModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class History extends BaseActivity {

    FragmentHIstoryBinding mBinding;
    HistoryAddapter historyAddapter;
    List<HistoryModel> historyModelList;

    public History() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = FragmentHIstoryBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

    }

    @Override
    protected void onResume() {
        super.onResume();

        getRides();

    }

    private void getRides() {

        showLoader();

        Query query = FirebaseFirestore.getInstance().collection("Bookings")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereIn("status", Arrays.asList("rated", "rideCompleted"));

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

        mBinding.rvHistory.setLayoutManager(new LinearLayoutManager(History.this));

        mBinding.rvHistory.setAdapter(new UpcomingRidesAdapter(History.this,
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