package com.buzzware.iride;

import android.app.Activity;
import android.app.Dialog;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iride.models.RideModel;
import com.buzzware.iride.models.User;
import com.buzzware.iride.models.VehicleModel;
import com.buzzware.iride.utils.AppConstants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RatingDialog extends Dialog {

    private RoundedImageView picRIV;
    private TextView nameTV;
    private TextView vehicleNoTV;
    private TextView budgetTV;
    private TextView ratingTV;
    private MaterialRatingBar userRating;
    private RelativeLayout submitBt;

    Activity c;

    public RatingDialog(@NonNull Activity context, RideModel ride, User user) {

        super(context);

        c = context;

        setContentView(R.layout.rating_dialog_lay);

        initView();

        setCancelable(false);

        setData(ride, user);

        setListeners(ride, user);

        getVehicleDetails(ride);
    }

    private void setListeners(RideModel ride, User user) {

        submitBt.setOnClickListener(v -> submitRating(user, ride));

    }

    private void submitRating(User user, RideModel ride) {

        if (user.ratings == null) {

            user.ratings = new ArrayList<>();

        }

        user.ratings.add(Float.valueOf(userRating.getRating()).doubleValue());

        FirebaseFirestore.getInstance().collection("Bookings").document(ride.id)
                .update("status", AppConstants.RideStatus.RATED);


        FirebaseFirestore.getInstance().collection("Users").document(ride.driverId)
                .update("ratings", user.ratings);


        dismiss();

    }

    private void getVehicleDetails(RideModel rideModel) {

        if(rideModel.vehicleId == null)

            return;

        FirebaseFirestore.getInstance().collection("Vehicle")
                .document(rideModel.vehicleId)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        VehicleModel vehicleDetails = task.getResult().toObject(VehicleModel.class);

                        if (vehicleDetails != null) {

                            vehicleNoTV.setText(vehicleDetails.tagNumber);

                        }

                    }

                });

    }

    private void setData(RideModel ride, User user) {

        nameTV.setText(user.firstName + " " + user.lastName);

        vehicleNoTV.setText(user.firstName + " " + user.lastName);

        budgetTV.setText(String.format("%.2f",Double.parseDouble(ride.price)) + "$");

//        budgetTV.setText(ride.price + "$");

        Glide.with(c).load(user.image).apply(new RequestOptions().placeholder(R.drawable.dummy_girl))
                .into(picRIV);

        if(user.ratings == null || user.ratings.size() == 0) {

            user.ratings = new ArrayList<>();

            ratingTV.setText("N/A");

        }

        double rating = 0;

        for (double r: user.ratings) {

            rating = rating + r;

        }

        rating = rating / Double.valueOf(user.ratings.size());

        ratingTV.setText(Math.round(rating)+"");

    }

    public void initView() {
        picRIV = (RoundedImageView) findViewById(R.id.picRIV);
        nameTV = (TextView) findViewById(R.id.nameTV);
        ratingTV = (TextView) findViewById(R.id.ratingTV);
        vehicleNoTV = (TextView) findViewById(R.id.vehicleNoTV);
        budgetTV = (TextView) findViewById(R.id.budgetTV);
        userRating = (MaterialRatingBar) findViewById(R.id.userRating);
        submitBt = (RelativeLayout) findViewById(R.id.submitBt);
    }
}
