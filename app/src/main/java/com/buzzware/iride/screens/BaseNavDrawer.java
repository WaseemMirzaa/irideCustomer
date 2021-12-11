package com.buzzware.iride.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.GravityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iride.R;
import com.buzzware.iride.databinding.AppBaseLayoutBinding;
import com.buzzware.iride.models.RideModel;
import com.buzzware.iride.models.User;
import com.buzzware.iride.utils.AppConstants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class BaseNavDrawer extends BaseActivity implements View.OnClickListener {

    AppBaseLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = AppBaseLayoutBinding.inflate(getLayoutInflater());

        super.setContentView(binding.getRoot());// The base layout that contains your navigation drawer.

        setBaseListeners();

        getCurrentUserData();

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

        if (!task.isSuccessful()) {

            openCloseDrawer();

            showErrorAlert(task.getException().getLocalizedMessage());

            return;
        }

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                rideModel = document.toObject(RideModel.class);

                rideModel.id = document.getId();


                startActivity(new Intent(BaseNavDrawer.this, HomeActivity.class));

                finish();

                return;

            }

        }

        hideLoader();

        openCloseDrawer();

        showErrorAlert("No Active Ride Found");

    }

    private void getCurrentUserData() {

        DocumentReference users = FirebaseFirestore.getInstance().collection("Users").document(getUserId());

        users.addSnapshotListener((value, error) -> {

            if (BaseNavDrawer.this != null && value != null) {

                User user = value.toObject(User.class);

                View headerLayout =
                        binding.navView.getHeaderView(0);

                if (user == null)

                    return;

                ImageView picIV = headerLayout.findViewById(R.id.picCIV);

                TextView nameTV = headerLayout.findViewById(R.id.nameTV);

                nameTV.setText(user.firstName + " " + user.lastName);

                if (BaseNavDrawer.this != null)

                    try {

                        Glide.with(this).load(user.image).apply(new RequestOptions().centerCrop().placeholder(R.drawable.dummy_girl)).into(picIV);

                    }catch (Exception e) {


                    }

            }


        });

    }

    protected void setBaseListeners() {

        binding.navView.findViewById(R.id.homeLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.bookingsLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.walletLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.profileLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.inviteLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.csLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.notificationLay).setOnClickListener(this);
        binding.navView.findViewById(R.id.activeRide).setOnClickListener(this);
        binding.navView.findViewById(R.id.schedulesLay).setOnClickListener(this);

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

        binding.stub.addView(view, params);

    }


    @Override
    public void onClick(View v) {
        if (v == binding.navView.findViewById(R.id.homeLay)) {

            showLoader();
            getActiveRide();


        } else if (v == binding.navView.findViewById(R.id.bookingsLay)) {

            openCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, BookingsActivity.class));
            finish();

        } else if (v == binding.navView.findViewById(R.id.walletLay)) {

            openCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, Wallet.class));
            finish();

        } else if (v == binding.navView.findViewById(R.id.profileLay)) {

            openCloseDrawer();
            startActivity(new Intent(BaseNavDrawer.this, Profile.class));
            finish();

        } else if (v == binding.navView.findViewById(R.id.inviteLay)) {

            openCloseDrawer();
            startActivity(new Intent(BaseNavDrawer.this, Invitation.class));
            finish();


        } else if (v == binding.navView.findViewById(R.id.notificationLay)) {

            openCloseDrawer();
            startActivity(new Intent(BaseNavDrawer.this, Notifications.class));
            finish();


        } else if (v == binding.navView.findViewById(R.id.csLay)) {

            openCloseDrawer();
            startActivity(new Intent(BaseNavDrawer.this, CustomerRequestsActivity.class));
            finish();

        } else if (v == binding.navView.findViewById(R.id.activeRide)) {

            openCloseDrawer();
            startActivity(new Intent(BaseNavDrawer.this, BookARideActivity.class));
            finish();
        } else if (v == binding.navView.findViewById(R.id.schedulesLay)) {

            openCloseDrawer();
            startActivity(new Intent(BaseNavDrawer.this, ScheduledRides.class));
            finish();
        }
    }

    public void openCloseDrawer() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {

            binding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            binding.drawerLayout.openDrawer(GravityCompat.START);

        }
    }

}
