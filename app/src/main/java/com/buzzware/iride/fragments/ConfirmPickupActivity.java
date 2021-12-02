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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfirmPickupActivity extends BaseNavDrawer implements OnMapReadyCallback, View.OnClickListener {

    FragmentConfirmPickupBinding mBinding;

    GoogleMap mMap;

    SearchedPlaceModel pickUpLocation, destinationLocation;

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

        }

    }

    private void init() {

        if (pickUpLocation != null) {

            mBinding.pickUpTV.setText(pickUpLocation.address);

        }


        if (destinationLocation != null) {

            mBinding.destinationTV.setText(destinationLocation.address);

        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
///move to dummy location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pickUpLocation.lat, pickUpLocation.lng), 12.0F));
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

    void getDirections() {

        String url = "/maps/api/directions/json?origin=" + pickUpLocation.lat + "," + pickUpLocation.lng + "&destination=" + destinationLocation.lat + "," + destinationLocation.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

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

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(pickUpLocation.lat, pickUpLocation.lng))
                .title("Pickup Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(destinationLocation.lat, destinationLocation.lng))
                .title("Destination")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

    }

    @Override
    public void onClick(View v) {

        super.onClick(v);

        if (v == mBinding.btnConfirmPickup) {

            //TODO Handle payment and then place order
//            ShowPaymentDialog();

            placeOrder();
        }
    }

    private void placeOrder() {

        RideModel rideModel = new RideModel();

        rideModel.bookingDate = new Date().getTime();

        rideModel.destination = destinationLocation;

        rideModel.pickUp = pickUpLocation;

        rideModel.userId = getUserId();

        rideModel.price = "100";

        rideModel.status = "booked";

        FirebaseFirestore.getInstance().collection("Bookings")
                .document().set(rideModel);

        Toast.makeText(this,"Successfully Booked",Toast.LENGTH_LONG).show();

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