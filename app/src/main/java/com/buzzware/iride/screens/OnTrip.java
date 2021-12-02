package com.buzzware.iride.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buzzware.iride.R;
import com.buzzware.iride.databinding.ActivityOnTripBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class OnTrip extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    ActivityOnTripBinding mBinding;

    public GoogleMap mMap;

    public Marker marker;

    public LatLng dummyLatLang= new LatLng(41.140630, -74.032660);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_on_trip);

        onViewCreated();
    }

    private void onViewCreated() {

        if (mBinding.homeMapView != null) {

            mBinding.homeMapView.onCreate(null);

            mBinding.homeMapView.onResume();

            mBinding.homeMapView.getMapAsync(this);

        }

        ///init click
        mBinding.btnTripView.setOnClickListener(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        ///move to dummy location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dummyLatLang, 16.0F));
    }

    @Override
    public void onClick(View v) {

        if(v == mBinding.btnTripView)
        {
            startActivity(new Intent(this, TripView.class));
        }
    }
}