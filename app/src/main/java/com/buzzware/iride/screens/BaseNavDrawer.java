package com.buzzware.iride.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.view.GravityCompat;

import com.buzzware.iride.R;
import com.buzzware.iride.databinding.AppBaseLayoutBinding;
import com.buzzware.iride.fragments.BookingsActivity;
import com.buzzware.iride.fragments.CustomerService;
import com.buzzware.iride.fragments.HomeActivity;
import com.buzzware.iride.fragments.Invitation;
import com.buzzware.iride.fragments.Profile;
import com.buzzware.iride.fragments.Wallet;
import com.buzzware.iride.models.RideModel;
import com.buzzware.iride.utils.AppConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class BaseNavDrawer extends BaseActivity implements View.OnClickListener {

    private FrameLayout view_stub; //This is the framelayout to keep your content view

    AppBaseLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = AppBaseLayoutBinding.inflate(getLayoutInflater());

        super.setContentView(binding.getRoot());// The base layout that contains your navigation drawer.

        setBaseListeners();

    }

    void getActiveRide() {

        Query query = FirebaseFirestore.getInstance().collection("Bookings")
                .whereEqualTo("userId", getUserId())
                .whereIn("status", Arrays.asList("driverAccepted", "driverReached", "rideStarted", "booked", AppConstants.RideStatus.RIDE_COMPLETED));

        query.get()
                .addOnCompleteListener(
                        this::parseSnapshot
                );
    }

    void parseSnapshot(Task<QuerySnapshot> task) {

        RideModel rideModel = null;

        hideLoader();

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                rideModel = document.toObject(RideModel.class);

                rideModel.id = document.getId();

                break;

            }

        }

        if (rideModel != null) {

            startActivity(new Intent(BaseNavDrawer.this, HomeActivity.class));

            finish();

        } else {

            showErrorAlert("No Active Ride Found");

        }

    }

    protected void setBaseListeners() {

        binding.navView.findViewById(R.id.homeLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.bookingsLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.walletLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.profileLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.inviteLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.csLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.activeRide).setOnClickListener(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (binding.stub != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            View stubView = inflater.inflate(layoutResID, binding.stub, false);
            binding.stub.addView(stubView, lp);
        }
    }

    @Override
    public void setContentView(View view) {
        if (binding.stub != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            binding.stub.addView(view, lp);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (binding.stub != null) {
            binding.stub.addView(view, params);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == binding.navView.findViewById(R.id.homeLay)) {

            OpenCloseDrawer();
            getActiveRide();


        } else if (v == binding.navView.findViewById(R.id.bookingsLay)) {

            OpenCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, BookingsActivity.class));
            finish();

        } else if (v == binding.navView.findViewById(R.id.walletLay)) {

            OpenCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, Wallet.class));
            finish();

        } else if (v == binding.navView.findViewById(R.id.profileLay)) {

            OpenCloseDrawer();
            startActivity(new Intent(BaseNavDrawer.this, Profile.class));
            finish();

        } else if (v == binding.navView.findViewById(R.id.inviteLay)) {

            OpenCloseDrawer();
            startActivity(new Intent(BaseNavDrawer.this, Invitation.class));
            finish();


        } else if (v == binding.navView.findViewById(R.id.csLay)) {

            OpenCloseDrawer();
            startActivity(new Intent(BaseNavDrawer.this, CustomerService.class));
            finish();

        } else if (v == binding.navView.findViewById(R.id.activeRide)) {

            OpenCloseDrawer();
            startActivity(new Intent(BaseNavDrawer.this, BookARideActivity.class));
            finish();
        }
    }

    public void OpenCloseDrawer() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {

            binding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            binding.drawerLayout.openDrawer(GravityCompat.START);

        }
    }

}
