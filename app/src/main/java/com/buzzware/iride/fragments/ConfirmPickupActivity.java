package com.buzzware.iride.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.buzzware.iride.R;
import com.buzzware.iride.databinding.AddCardDialogBinding;
import com.buzzware.iride.databinding.FragmentConfirmPickupBinding;
import com.buzzware.iride.databinding.PaymentDialogBinding;
import com.buzzware.iride.models.RideModel;
import com.buzzware.iride.models.SearchedPlaceModel;
import com.buzzware.iride.models.TripDetail;
import com.buzzware.iride.response.directions.DirectionsApiResponse;
import com.buzzware.iride.response.directions.Leg;
import com.buzzware.iride.response.directions.Route;
import com.buzzware.iride.response.directions.Step;
import com.buzzware.iride.retrofit.Controller;
import com.buzzware.iride.screens.BaseNavDrawer;
import com.buzzware.iride.utils.AppConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfirmPickupActivity extends BaseNavDrawer implements OnMapReadyCallback, View.OnClickListener {

    FragmentConfirmPickupBinding mBinding;

    GoogleMap mMap;

    SearchedPlaceModel pickUpLocation, destinationLocation, secondDropOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        onCreateView();

    }

    private void onCreateView() {

        mBinding = FragmentConfirmPickupBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        getExtrasFromIntent();

        init();

        setListeners();

    }

    private void setListeners() {

        mBinding.btnConfirmPickup.setOnClickListener(this);

        mBinding.pricingRG.setOnCheckedChangeListener(this::onCheckedChanged);


    }

    @SuppressLint("SetTextI18n")
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (checkedId == mBinding.iRideRB.getId()) {

            mBinding.estimateTV.setText("NPR 100");

        } else if (checkedId == mBinding.luxRB.getId()) {

            mBinding.estimateTV.setText("NPR 150");

        } else {

            mBinding.estimateTV.setText("NPR 200");

        }

    }

    private void getExtrasFromIntent() {

        if (getIntent().getExtras() != null) {

            Bundle b = getIntent().getExtras();

            pickUpLocation = b.getParcelable("pickup");

            destinationLocation = b.getParcelable("destination");

            if (b.getParcelable("secondDropOff") != null)

                secondDropOff = b.getParcelable("secondDropOff");

        }

    }

    private void init() {

        if (pickUpLocation != null) {

            mBinding.pickUpTV.setText(pickUpLocation.address);

        }

        if (destinationLocation != null) {

            mBinding.destinationTV.setText(destinationLocation.address);

        }

        if(secondDropOff != null) {

            mBinding.dropOffLL.setVisibility(View.VISIBLE);
            mBinding.dropOffVw.setVisibility(View.VISIBLE);

            mBinding.dropOffTV.setText(secondDropOff.address);

        } else {

            mBinding.dropOffLL.setVisibility(View.GONE);
            mBinding.dropOffVw.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
///move to dummy location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pickUpLocation.lat, pickUpLocation.lng), 12.0F));

        calledForSecondDropOff = false;

        getDirections();
    }

    @Override
    protected void onResume() {
        super.onResume();

        resumeMap();

    }

    private void resumeMap() {

        mBinding.homeMapView.onCreate(null);

        mBinding.homeMapView.onResume();

        mBinding.homeMapView.getMapAsync(this);

    }

    Call<String> reverseCall;

    Boolean calledForSecondDropOff = false;

    void getDirections() {

        String url = null;

        if (!calledForSecondDropOff) {

            url = "/maps/api/directions/json?origin=" + pickUpLocation.lat + "," + pickUpLocation.lng + "&destination=" + destinationLocation.lat + "," + destinationLocation.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        } else {

            url = "/maps/api/directions/json?origin=" + destinationLocation.lat + "," + destinationLocation.lng + "&destination=" + secondDropOff.lat + "," + secondDropOff.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        }

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi().getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DirectionsApiResponse resp = gson.fromJson(response.body(), DirectionsApiResponse.class);

                    drawPaths(resp);

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    private void drawPaths(DirectionsApiResponse res) {

        ArrayList<LatLng> path = new ArrayList<>();

        try {

            if (res.routes != null && res.routes.size() > 0) {

                Route route = res.routes.get(0);

                if (route.legs != null) {

                    for (int i = 0; i < route.legs.size(); i++) {

                        Leg leg = route.legs.get(i);

                        if (leg.steps != null) {

                            for (int j = 0; j < leg.steps.size(); j++) {

                                Step step1 = leg.steps.get(j);

                                if (step1.polyline != null) {

                                    List<LatLng> decoded = PolyUtil.decode(step1.polyline.points);

                                    path.addAll(decoded);

                                }

                            }

                        }

                    }

                }

            }

        } catch (Exception ex) {

        }

        //Draw the polyline
        if (path.size() > 0) {

            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLACK).width(10);

            mMap.addPolyline(opts);

        }

        if (!calledForSecondDropOff) {

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(pickUpLocation.lat, pickUpLocation.lng))
                    .title("Pickup Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(destinationLocation.lat, destinationLocation.lng))
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            if (secondDropOff != null) {

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(secondDropOff.lat, secondDropOff.lng))
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                calledForSecondDropOff = true;
                getDirections();

            }
        }
    }

    @Override
    public void onClick(View v) {

        super.onClick(v);

        if (v == mBinding.btnConfirmPickup) {

            //TODO Handle payment and then place order
//            ShowPaymentDialog();

            getActiveRide();
        }
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

            showErrorAlert("Already have an active ride");

        } else {

            placeOrder();

        }

    }

    private void placeOrder() {

        RideModel rideModel = new RideModel();

        rideModel.bookingDate = new Date().getTime();

        rideModel.tripDetail = new TripDetail();

        rideModel.tripDetail.destinations = new ArrayList<>();

        rideModel.tripDetail.destinations.add(destinationLocation);

        if (secondDropOff != null) {

            rideModel.tripDetail.destinations.add(secondDropOff);

        }

        rideModel.tripDetail.pickUp = pickUpLocation;

        rideModel.userId = getUserId();

        rideModel.price = "100";

        rideModel.status = "booked";

        FirebaseFirestore.getInstance().collection("Bookings")
                .document().set(rideModel);

        Toast.makeText(this, "Successfully Booked", Toast.LENGTH_LONG).show();

        startActivity(new Intent(this, HomeActivity.class)
                .addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
        );

        finish();

    }

    private void ShowPaymentDialog() {

        Dialog myDialog = new Dialog(this);

        PaymentDialogBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(myDialog.getContext()),
                R.layout.payment_dialog,
                myDialog.findViewById(R.id.parentt),
                false);

        myDialog.setContentView(mBinding.getRoot());

        myDialog.setCancelable(true);

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        myDialog.show();

        mBinding.cardItem.setOnClickListener(v -> {

            myDialog.dismiss();

        });

        mBinding.btnAddPayment.setOnClickListener(v -> {
            myDialog.dismiss();
            ShowAddPaymentDialog();
        });
    }

    private void ShowAddPaymentDialog() {

        Dialog myDialog = new Dialog(this);

        AddCardDialogBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(myDialog.getContext()),
                R.layout.add_card_dialog,
                myDialog.findViewById(R.id.parent),
                false);

        myDialog.setContentView(mBinding.getRoot());

        myDialog.setCancelable(true);

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        myDialog.show();
    }
}