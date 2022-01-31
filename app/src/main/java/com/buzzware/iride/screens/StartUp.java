package com.buzzware.iride.screens;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.buzzware.iride.R;
import com.buzzware.iride.databinding.ActivityStartupBinding;
import com.buzzware.iride.models.RideModel;
import com.buzzware.iride.utils.AppConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

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

            startActivity(new Intent(StartUp.this, BookARideActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

            finish();

        } else {

            startActivity(new Intent(this, Authentication.class));
        }
    }

    void getActiveRide() {

        showLoader();

        Query query = FirebaseFirestore.getInstance().collection("Bookings")
                .whereEqualTo("userId", getUserId())
                .whereIn("status", Arrays.asList("driverAccepted", "driverReached", "reBooked","rideStarted", "booked", AppConstants.RideStatus.RIDE_COMPLETED));

        query.get()
                .addOnCompleteListener(
                        this::parseBaseSnapshot
                );
    }

    void parseBaseSnapshot(Task<QuerySnapshot> task) {

        RideModel rideModel = null;

        hideLoader();

        if (!task.isSuccessful()) {

            showErrorAlert(task.getException().getLocalizedMessage());

            return;
        }

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                rideModel = document.toObject(RideModel.class);

                rideModel.id = document.getId();

//                startActivity(new Intent(StartUp.this, HomeActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

                finish();

                return;

            }

        }

        startActivity(new Intent(StartUp.this, BookARideActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

        finish();

    }


}