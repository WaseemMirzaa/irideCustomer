package com.buzzware.iride.screens;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.buzzware.iride.databinding.FragmentExpandablePlacesListBinding;
import com.buzzware.iride.fragments.ConfirmPickupActivity;
import com.buzzware.iride.fragments.ExpandablePlacesListFragment;
import com.buzzware.iride.response.geoCode.ReverseGeoCode;
import com.buzzware.iride.response.geoCode.ReverseGeoCodeResponse;
import com.buzzware.iride.models.SearchedPlaceModel;
import com.buzzware.iride.OnPredictedEvent;
import com.buzzware.iride.OnTextChangedEvent;
import com.buzzware.iride.R;
import com.buzzware.iride.utils.AppConstants;
import com.buzzware.iride.databinding.FragmentBookARideBinding;
import com.buzzware.iride.retrofit.Controller;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

enum CurrentSelection {

    whereTo, currentLocation

}

public class BookARideActivity extends BaseNavDrawer implements OnMapReadyCallback {

    FragmentBookARideBinding mBinding;

    Context context;

    public GoogleMap mMap;

    CurrentSelection currentSelection = CurrentSelection.whereTo;

    private SimpleLocation location;

    Boolean hasLocationPermissions;

    SearchedPlaceModel placeWhereTo, placeCurrentLocation;

    Boolean isChangedFromMap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = FragmentBookARideBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        checkPermissionsAndInit();

        mBinding.backIcon.setOnClickListener(v -> OpenCloseDrawer());

    }

    @Override
    protected void onResume() {
        super.onResume();

        checkIfPermissionsGranted();
    }

    private void checkIfPermissionsGranted() {

        if (ContextCompat.checkSelfPermission(BookARideActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(BookARideActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(BookARideActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        hasLocationPermissions = true;

        location = new SimpleLocation(BookARideActivity.this);

        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            showEnableLocationDialog("Please enable location from setting in order to proceed to the app");

            return;
        }

        location.beginUpdates();

        init();

        setListeners();

    }

    private void checkPermissionsAndInit() {

        currentSelection = CurrentSelection.whereTo;

        String[] permissions = {Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


        Permissions.check(BookARideActivity.this/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                hasLocationPermissions = true;

                location = new SimpleLocation(BookARideActivity.this);

                if (!location.hasLocationEnabled()) {
                    // ask the user to enable location access
                    showEnableLocationDialog("Please enable location from setting in order to proceed to the app");

                    return;
                }

                location.beginUpdates();

                init();

                setListeners();

                ExpandablePlacesListFragment expandablePlacesListFragment = new ExpandablePlacesListFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.bslContainer, expandablePlacesListFragment).addToBackStack("bslContainer").commit();

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                hasLocationPermissions = false;

                showPermissionsDeniedError("Please enable location permissions from setting in order to proceed to the app.");

            }
        });
    }

    @Override
    public void onPause() {

        disableLocationUpdates();

        super.onPause();
    }

    private void disableLocationUpdates() {

        try {

            location.endUpdates();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }


    private void setListeners() {

        mBinding.currentLocationET.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {

                EventBus.getDefault().post(new ExpandablePlacesListFragment.ShowBottomSheetMsg());

                currentSelection = CurrentSelection.currentLocation;

            }
        });
        mBinding.destinationET.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {

                EventBus.getDefault().post(new ExpandablePlacesListFragment.ShowBottomSheetMsg());

                currentSelection = CurrentSelection.whereTo;

            }
        });

        mBinding.currentLocationET.setOnTouchListener((v, event) -> {

            EventBus.getDefault().post(new ExpandablePlacesListFragment.ShowBottomSheetMsg());

            currentSelection = CurrentSelection.currentLocation;

            return false;

        });

        mBinding.homeMapView.setOnTouchListener((v, event) -> {

            isChangedFromMap = true;

            return false;
        });

        mBinding.destinationET.setOnTouchListener((v, event) -> {

            EventBus.getDefault().post(new ExpandablePlacesListFragment.ShowBottomSheetMsg());

            currentSelection = CurrentSelection.whereTo;

            return false;
        });

        mBinding.currentLocationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isChangedFromMap) {

                    currentSelection = CurrentSelection.currentLocation;

                    OnTextChangedEvent event = new OnTextChangedEvent();

                    event.data = s.toString();

                    EventBus.getDefault().post(event);
                }

                isChangedFromMap = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.destinationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isChangedFromMap) {

                    currentSelection = CurrentSelection.whereTo;

                    OnTextChangedEvent event = new OnTextChangedEvent();

                    event.data = s.toString();

                    EventBus.getDefault().post(event);
                }

                isChangedFromMap = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void init() {

        if (mBinding.homeMapView != null) {

            mBinding.homeMapView.onCreate(null);

            mBinding.homeMapView.onResume();

            mBinding.homeMapView.getMapAsync(this);
        }
        ///init click
        mBinding.btnNext.setOnClickListener(v -> moveToConfirmPickup());
    }

    private void moveToConfirmPickup() {

        if (placeCurrentLocation == null || placeWhereTo == null) {

            showErrorAlert("Please select destination first");

            return;
        }


        Intent i = new Intent(BookARideActivity.this, ConfirmPickupActivity.class);

        i.putExtra("pickup", placeCurrentLocation);

        i.putExtra("destination", placeWhereTo);

        startActivity(i);

        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnPredictedEvent event) {

        hideKeyboard();

        reverseGeoCode(event.place.geometry.location.lat, event.place.geometry.location.lng);

    }

    Call<String> reverseCall;

    void reverseGeoCode(double lat, double lng) {

        String url = "/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi().getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    ReverseGeoCodeResponse reverseGeoCodeResponse = gson.fromJson(response.body(), ReverseGeoCodeResponse.class);

                    setLocationDetails(reverseGeoCodeResponse);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                reverseCall = null;
            }
        });
    }

    private void setLocationDetails(ReverseGeoCodeResponse response) {

        SearchedPlaceModel searchedPlaceModel = new SearchedPlaceModel();

        List<ReverseGeoCode> reverseGeoCodeList = response.results;

        if (reverseGeoCodeList == null || reverseGeoCodeList.size() == 0)

            return;

        searchedPlaceModel.address = reverseGeoCodeList.get(0).formatted_address;
        searchedPlaceModel.lat = reverseGeoCodeList.get(0).geometry.location.lat;
        searchedPlaceModel.lng = reverseGeoCodeList.get(0).geometry.location.lng;

        if (currentSelection == CurrentSelection.currentLocation) {

            if (placeCurrentLocation == null) {

                currentSelection = CurrentSelection.whereTo;

            }

            placeCurrentLocation = searchedPlaceModel;

//            disableTextWatchers();

            mBinding.currentLocationET.setText(searchedPlaceModel.address);

        } else {

            placeWhereTo = searchedPlaceModel;

//            disableTextWatchers();

            mBinding.destinationET.setText(searchedPlaceModel.address);

        }

        setListeners();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        setMarkers();

        currentSelection = CurrentSelection.currentLocation;
        reverseGeoCode(location.getLatitude(), location.getLongitude());
    }

    private void setMarkers() {

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0F));


        // Enable GPS marker in Map
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 500, null);

//         mMap.addMarker(new MarkerOptions()
//                .position(currentLatLng)
//                .title("Select Location")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        mMap.setOnCameraIdleListener(() -> {

//            isChangedFromMap = true;

            LatLng midLatLng = mMap.getCameraPosition().target;

            reverseGeoCode(midLatLng.latitude, midLatLng.longitude);

//            if (marker != null) {
//
//                marker.setPosition(midLatLng);

//            }
        });
    }
}