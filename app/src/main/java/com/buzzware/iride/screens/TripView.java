package com.buzzware.iride.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.buzzware.iride.R;
import com.buzzware.iride.databinding.ActivityTripViewBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TripView extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    ActivityTripViewBinding mBinding;
    public static GoogleMap mMap;
    public static Marker myMarker;
    public LatLng dummyLatLang= new LatLng(41.140630, -74.032660);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_trip_view);
        if (mBinding.homeMapView != null) {
            mBinding.homeMapView.onCreate(null);
            mBinding.homeMapView.onResume();
            mBinding.homeMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        ///move to dummy location
        myMarker = mMap.addMarker(new MarkerOptions().position(dummyLatLang).title("Golden Gate Bridge").icon(BitmapFromVector(this, R.drawable.triangle_marker)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dummyLatLang, 16.0F));
        googleMap.setOnMarkerClickListener(this);
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(20, 20, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if(marker.equals(myMarker))
        {
            ShowRatingDailog();
        }
        return true;
    }

    private void ShowRatingDailog() {
        Dialog myDialog= new Dialog(this);
        myDialog.setContentView(R.layout.rating_dialog_lay);
        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}