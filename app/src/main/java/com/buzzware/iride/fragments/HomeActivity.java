package com.buzzware.iride.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iride.R;
import com.buzzware.iride.RatingDialog;
import com.buzzware.iride.models.RideModel;
import com.buzzware.iride.models.User;
import com.buzzware.iride.models.VehicleModel;
import com.buzzware.iride.response.directions.DirectionsApiResponse;
import com.buzzware.iride.response.directions.Leg;
import com.buzzware.iride.response.directions.Route;
import com.buzzware.iride.response.directions.Step;
import com.buzzware.iride.response.distanceMatrix.DistanceMatrixResponse;
import com.buzzware.iride.response.distanceMatrix.Element;
import com.buzzware.iride.response.distanceMatrix.Row;
import com.buzzware.iride.retrofit.Controller;
import com.buzzware.iride.screens.BaseNavDrawer;
import com.buzzware.iride.screens.BookARideActivity;
import com.buzzware.iride.databinding.FragmentHomeBinding;
import com.buzzware.iride.utils.AppConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class HomeActivity extends BaseNavDrawer implements OnMapReadyCallback {

    FragmentHomeBinding mBinding;

    Context context;

    public GoogleMap mMap;

    public Marker locationMarker, driverMarker, destinationMarker;

    private SimpleLocation location;

    RatingDialog ratingDialog;

    Boolean hasLocationPermissions;

    RideModel rideModel;

    Polyline polyline;

    LatLng pastLatLng;

    VehicleModel vehicleDetails;

    public HomeActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = FragmentHomeBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        mBinding.homeMapView.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        checkPermissionsAndInit();

    }

    private void checkPermissionsAndInit() {

        String[] permissions = {Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        Permissions.check(this/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                hasLocationPermissions = true;

                location = new SimpleLocation(HomeActivity.this);

                if (!location.hasLocationEnabled()) {
                    // ask the user to enable location access
                    showEnableLocationDialog("Please enable location from setting in order to proceed to the app");

                    return;
                }

                location.beginUpdates();

                init();

                setListeners();
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

        mBinding.btnWhereGo.setOnClickListener(v -> onWhereToClicked());

        mBinding.btnSettings.setOnClickListener(v -> onSettingsClicked());

        mBinding.currentLocationIV.setOnClickListener(v -> onCurrentLocationClicked());

        mBinding.btnDrawer.setOnClickListener(v -> onDrawerClicked());
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

            if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0F));

            }

            setEventListener(rideModel);

        }

    }

    private void setEventListener(RideModel r) {

        FirebaseFirestore.getInstance().collection("Bookings")
                .document(r.id)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {

                        RideModel rideModel = value.toObject(RideModel.class);

                        if (rideModel != null) {

                            rideModel.id = r.id;

                        }

                        if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

                            showRideMarkers(rideModel);

                            mBinding.onTripLL.setVisibility(View.VISIBLE);

                            mBinding.reachingLL.setVisibility(View.GONE);

                        } else if (AppConstants.RideStatus.isRideDriverArriving(rideModel.status)) {

                            showRideMarkers(rideModel);

                            mBinding.onTripLL.setVisibility(View.GONE);

                            mBinding.reachingLL.setVisibility(View.VISIBLE);

                        } else if (AppConstants.RideStatus.BOOKED.equalsIgnoreCase(rideModel.status)) {

                            this.rideModel = rideModel;

                            setWaitingForDriver(rideModel);

                        } else {

                            showRideMarkers(rideModel);

                        }
                    }

                });
    }

    Marker bookedPickupMarker, bookedDestinationMarker;

    private void setWaitingForDriver(RideModel rideModel) {

        LatLng currentLatLng = new LatLng(rideModel.pickUp.lat, rideModel.pickUp.lng);

        bookedPickupMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("").icon(BitmapFromVector(context, R.drawable.destination)));

        LatLng dest = new LatLng(rideModel.destination.lat, rideModel.destination.lng);

        bookedDestinationMarker = mMap.addMarker(new MarkerOptions().position(dest).title("").icon(BitmapFromVector(context, R.drawable.destination)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0F));

        getDirections(rideModel.pickUp.lat, rideModel.pickUp.lng, rideModel.destination.lat, rideModel.destination.lng);

        mBinding.searchingForDrivers.setText("Searching For Drivers");

        mBinding.kmTV.setText("Loading");

    }

    private void showRideMarkers(RideModel ride) {

        rideModel = ride;

        hideBookedMarkers();

        if (rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.RIDE_COMPLETED)) {

            setDriverListener();

            return;

        }

        mBinding.actionTV.setOnClickListener(v -> {

            FirebaseFirestore.getInstance().collection("Bookings")
                    .document(ride.id)
                    .update("status", AppConstants.RideStatus.CANCELLED);

            // todo finish and start new activity

        });

        if (AppConstants.RideStatus.isRideDriverArriving(ride.status)) {

            if (locationMarker != null) {

                return;

            }

            LatLng currentLatLng = new LatLng(ride.pickUp.lat, ride.pickUp.lng);

            locationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("").icon(BitmapFromVector(context, R.drawable.destination)));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0F));

            setDriverListener();

        } else if (AppConstants.RideStatus.isRideInProgress(ride.status)) {

            if (locationMarker != null) {

                locationMarker.remove();

                locationMarker = null;

            }

            if (destinationMarker != null) {

                return;

            }

            LatLng currentLatLng = new LatLng(ride.destination.lat, ride.destination.lng);

            destinationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("").icon(BitmapFromVector(context, R.drawable.destination)));

            setDriverListener();

        }
    }

    private void hideBookedMarkers() {

        if(bookedPickupMarker != null) {

            bookedPickupMarker.remove();
            bookedPickupMarker = null;

        }

        if(bookedDestinationMarker != null) {

            bookedDestinationMarker.remove();

            bookedDestinationMarker = null;

        }
    }

    Call<String> reverseCall;

    void getDirections(double originLat, double originLng, double destinationLat, double destinationLng) {

        String url = "/maps/api/directions/json?origin=" + originLat + "," + originLng + "&destination=" + destinationLat + "," + destinationLng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

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

                    if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

                        calculateDistance();

                    }
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

            if (polyline != null)

                polyline.remove();

            polyline = null;

            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLACK).width(10);

            polyline = mMap.addPolyline(opts);


        }
    }

    private void setDriverListener() {

        FirebaseFirestore.getInstance().collection("Users")
                .document(rideModel.driverId)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {

                        User user = value.toObject(User.class);

                        if (rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.RIDE_COMPLETED)) {

                            if (ratingDialog != null && ratingDialog.isShowing()) {

                                return;
                            }

                            ratingDialog = new RatingDialog(HomeActivity.this, rideModel, user);

                            ratingDialog.show();

                            return;

                        }

                        updateDriverLocation(user);

                        setUserData(user);

                        setPhoneListener(user);

                        if (vehicleDetails == null) {

                            getVehicleDetails();

                        }

                    }

                });

    }

    void calculateDistance() {

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + location.getLatitude() + "," + location.getLongitude() + "&destinations=" + rideModel.destination.lat + "," + rideModel.destination.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

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

                    DistanceMatrixResponse resp = gson.fromJson(response.body(), DistanceMatrixResponse.class);

                    setDistance(resp);

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    private void setDistance(DistanceMatrixResponse resp) {

        String distance = "";
        String time = "";
        String currentAddress = "";

        if (resp.origin_addresses != null && resp.origin_addresses.size() > 0) {

            currentAddress = resp.origin_addresses.get(0);

        }

        if (resp.rows != null && resp.rows.size() > 0) {

            Row row = resp.rows.get(0);

            if (row.elements != null && row.elements.size() > 0) {

                Element element = row.elements.get(0);

                if (element.distance != null)

                    distance = element.distance.text;

                if (element.duration != null)

                    time = element.duration.text;

            }

        }

        mBinding.timeTV.setText(time);
        mBinding.kmTV.setText(distance);
    }


    private void getVehicleDetails() {

        showLoader();

        FirebaseFirestore.getInstance().collection("Vehicle")
                .document(rideModel.vehicleId)
                .get()
                .addOnCompleteListener(task -> {

                    hideLoader();

                    if (task.isSuccessful()) {

                        vehicleDetails = task.getResult().toObject(VehicleModel.class);

                        if (vehicleDetails != null) {

                            setVehicleData();

                        }

                    }

                });


    }

    private void setVehicleData() {

        mBinding.numberTV.setText(vehicleDetails.tagNumber);

        mBinding.carTitleTV.setText(vehicleDetails.name);

        mBinding.colorTV.setText(vehicleDetails.make);

        Glide.with(HomeActivity.this).load(vehicleDetails.frontCarUrl).apply(new RequestOptions().centerCrop()).into(mBinding.carPic);

    }

    private void setUserData(User user) {

        mBinding.nameTV.setText(user.firstName + " " + user.lastName);

        //Todo make rating hardcoded
        mBinding.ratingTV.setText("4.5");

    }

    private void setPhoneListener(User user) {

        mBinding.callIV.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + user.phoneNumber));
            startActivity(intent);

        });
    }

    private double radiansToDegrees(double x) {
        return x * 180.0 / Math.PI;
    }

    private void updateDriverLocation(User user) {

        LatLng currentLatLng = new LatLng(user.lat, user.lng);

        if (driverMarker == null)

            driverMarker = mMap
                    .addMarker(
                            new MarkerOptions()
                                    .position(currentLatLng)
                                    .title("")
                                    .icon(
                                            BitmapFromVector(
                                                    context, R.drawable.car)
                                    )
                    );

        else {

            if (pastLatLng == null) {

                pastLatLng = currentLatLng;

            }

            driverMarker.setPosition(currentLatLng);


            double fLat = (Math.PI * pastLatLng.latitude) / 180.0f;
            double fLng = (Math.PI * pastLatLng.longitude) / 180.0f;
            double tLat = (Math.PI * currentLatLng.latitude) / 180.0f;
            double tLng = (Math.PI * currentLatLng.longitude) / 180.0f;

            double degree = radiansToDegrees(Math.atan2(sin(tLng - fLng) * cos(tLat), cos(fLat) * sin(tLat) - sin(fLat) * cos(tLat) * cos(tLng - fLng)));

            double bearing = 0;

            if (degree >= 0) {
                bearing = degree;
            } else {
                bearing = 360 + degree;
            }

            driverMarker.setRotation((float) bearing);

            pastLatLng = currentLatLng;

        }

        if (AppConstants.RideStatus.isRideDriverArriving(rideModel.status)) {

            getDirections(user.lat, user.lng, rideModel.pickUp.lat, rideModel.pickUp.lng);

        } else {

            getDirections(user.lat, user.lng, rideModel.destination.lat, rideModel.destination.lng);

        }

    }

    private void onCurrentLocationClicked() {

        if (!hasLocationPermissions) {

            return;
        }

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0F));
    }

    private void init() {

        context = HomeActivity.this;

        mBinding.homeMapView.onResume();

        mBinding.homeMapView.getMapAsync(this);
    }

    private void onDrawerClicked() {

        SetStatusBarColor();

        OpenCloseDrawer();
    }

    private void onSettingsClicked() {

    }

    private void onWhereToClicked() {

        startActivity(new Intent(HomeActivity.this, BookARideActivity.class));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        getActiveRide();

    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(20, 20, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public void SetStatusBarColor() {

    }

}