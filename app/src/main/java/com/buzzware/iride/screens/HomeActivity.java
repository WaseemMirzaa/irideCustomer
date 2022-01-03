package com.buzzware.iride.screens;

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
import com.buzzware.iride.Firebase.FirebaseInstances;
import com.buzzware.iride.R;
import com.buzzware.iride.RatingDialog;
import com.buzzware.iride.models.RideModel;
import com.buzzware.iride.models.SearchedPlaceModel;
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
import com.google.firebase.firestore.ListenerRegistration;
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

import static com.buzzware.iride.retrofit.Controller.Base_Url;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class HomeActivity extends BaseNavDrawer implements OnMapReadyCallback {

    FragmentHomeBinding mBinding;

    String previousStatus;

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

//        mBinding.btnWhereGo.setOnClickListener(v -> onWhereToClicked());

        mBinding.btnSettings.setOnClickListener(v -> onSettingsClicked());

        mBinding.currentLocationIV.setOnClickListener(v -> onCurrentLocationClicked());

        mBinding.btnDrawer.setOnClickListener(v -> onDrawerClicked());


    }

    private void startChat(User user) {

        Intent intent = new Intent(HomeActivity.this, MessagesActivity.class);
        intent.putExtra("conversationID", "no id");
        intent.putExtra("selectedUserID", user.id);
        intent.putExtra("selectedUserName", user.firstName);
        intent.putExtra("checkFrom", "true");
        startActivity(intent);

    }

    void getActiveRide() {

        Query query = FirebaseFirestore.getInstance().collection("Bookings")
                .whereEqualTo("userId", getUserId())
                .whereIn("status", Arrays.asList(
                        "driverAccepted",
                        "driverReached",
                        "rideStarted",
                        "booked", "reBooked",
                        AppConstants.RideStatus.RIDE_COMPLETED
                ));

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

    ListenerRegistration listenerRegistration;

    private void setEventListener(RideModel r) {

        FirebaseFirestore.getInstance().collection("Bookings")
                .document(r.id)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {

                        RideModel rideModel = value.toObject(RideModel.class);

                        if (rideModel != null) {

                            rideModel.id = r.id;

                            mBinding.whereToTV.setText(rideModel.tripDetail.pickUp.address);

                            if(((previousStatus != null && previousStatus.equalsIgnoreCase(AppConstants.RideStatus.DRIVER_ACCEPTED)) && rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.DRIVER_REACHED)) || (previousStatus == null && rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.DRIVER_REACHED))) {

                                showErrorAlert("Your Driver has arrived");

                            }

                            previousStatus = rideModel.status;

                        }

                        if (this.rideModel != null) {

                            removePreviousPolyline(rideModel);

                        }

                        if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

                            mBinding.searchingForDrivers.setText("Have safe trip");

                            //User is in car moving towards destination

                            showRideMarkers(rideModel);

                            mBinding.onTripLL.setVisibility(View.VISIBLE);

                            mBinding.reachingLL.setVisibility(View.GONE);

                        } else if (AppConstants.RideStatus.isRideDriverArriving(rideModel.status)) {

                            //Driver is Arriving

                            showRideMarkers(rideModel);

                            mBinding.onTripLL.setVisibility(View.GONE);

                            mBinding.reachingLL.setVisibility(View.VISIBLE);

                        } else if (AppConstants.RideStatus.BOOKED.equalsIgnoreCase(rideModel.status) || AppConstants.RideStatus.RE_BOOKED.equalsIgnoreCase(rideModel.status)) {

                            //Drive Booked But displaying Waiting for driver popup


                            if (driverMarker != null) {

                                driverMarker.remove();

                            }

                            if (polyline != null)

                                polyline.remove();

                            hideSecondPolyline();

                            if (listenerRegistration != null)

                                listenerRegistration.remove();

                            listenerRegistration = null;

                            this.rideModel = rideModel;

                            setWaitingForDriver(rideModel);

                            mBinding.reachingLL.setVisibility(View.GONE);

                            mBinding.onTripLL.setVisibility(View.VISIBLE);


                        } else {

                            showRideMarkers(rideModel);

                        }
                    }

                });
    }

    private void removePreviousPolyline(RideModel rideModel) {
        int size = rideModel.tripDetail.destinations.size();


        Boolean statusUpdated = false;

        if (size > 2) {

            String status1 = rideModel.tripDetail.destinations.get(0).status;
            String status2 = this.rideModel.tripDetail.destinations.get(0).status;

            statusUpdated = !status1.equalsIgnoreCase(status2);

        }

        if (!rideModel.status.equalsIgnoreCase(this.rideModel.status) || statusUpdated) {

            if (polyline != null)

                polyline.remove();

            if (secondDropOffPolyline != null)

                secondDropOffPolyline.remove();

            if (statusUpdated) {

                destinationMarker2.remove();
            }
        }
    }

    Marker bookedPickupMarker, bookedDestinationMarker, lastDestinationMarker, destinationMarker2;

    private void setWaitingForDriver(RideModel rideModel) {

        LatLng currentLatLng = new LatLng(rideModel.tripDetail.pickUp.lat, rideModel.tripDetail.pickUp.lng);

        bookedPickupMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title(""));

        LatLng dest = new LatLng(rideModel.tripDetail.destinations.get(0).lat, rideModel.tripDetail.destinations.get(0).lng);

        bookedDestinationMarker = mMap.addMarker(new MarkerOptions().position(dest).title(""));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0F));

        getDirections(rideModel.tripDetail.pickUp.lat, rideModel.tripDetail.pickUp.lng, rideModel.tripDetail.destinations.get(0).lat, rideModel.tripDetail.destinations.get(0).lng, false);

        if (rideModel.tripDetail.destinations.size() > 1) {

            LatLng secondDest = new LatLng(rideModel.tripDetail.destinations.get(1).lat, rideModel.tripDetail.destinations.get(1).lng);

            lastDestinationMarker = mMap.addMarker(new MarkerOptions().position(secondDest).title(""));

        }

        mBinding.searchingForDrivers.setText("Searching For Drivers");

        mBinding.kmTV.setText("Loading");

    }

    private void showRideMarkers(RideModel ride) {

        rideModel = ride;

        hideBookedMarkers();

        if (rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.RIDE_COMPLETED)) {

            //ride completed show rating popup

            mBinding.searchingForDrivers.setText("Have safe trip");


            setDriverListener();

            return;

        }


        mBinding.actionTV.setOnClickListener(v -> {

            FirebaseFirestore.getInstance().collection("Bookings")
                    .document(ride.id)
                    .update("status", AppConstants.RideStatus.CANCELLED);

            if (ride.driverId != null)
                
                FirebaseInstances.usersCollection.document(ride.driverId)
                        .update("isActive", true);

            startActivity(new Intent(HomeActivity.this, BookARideActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

            finish();

        });

        if (AppConstants.RideStatus.isRideDriverArriving(ride.status)) {

            if (locationMarker != null) {

                //Driver is arriving showing driver location on map

                return;

            }

            //driver is arriving adding driver location marker to map

            LatLng currentLatLng = new LatLng(ride.tripDetail.pickUp.lat, ride.tripDetail.pickUp.lng);

            locationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title(""));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0F));

            setDriverListener();

        } else if (AppConstants.RideStatus.isRideInProgress(ride.status)) {

            //ride is in progress show ride markers

            if (locationMarker != null) {

                locationMarker.remove();

                locationMarker = null;

            }

            if (destinationMarker != null) {

                return;

            }

            if (ride.tripDetail.destinations.size() > 1) {

                //For multiple DropOff

                if (rideModel.tripDetail.destinations.get(0).status.equalsIgnoreCase(AppConstants.RideDetailStatus.NOT_REACHED)) {


                    LatLng currentLatLng = new LatLng(ride.tripDetail.destinations.get(0).lat, ride.tripDetail.destinations.get(0).lng);

                    destinationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title(""));
                }

                LatLng destination2 = new LatLng(ride.tripDetail.destinations.get(1).lat, ride.tripDetail.destinations.get(1).lng);

                destinationMarker2 = mMap.addMarker(new MarkerOptions().position(destination2).title(""));

            } else {

                LatLng currentLatLng = new LatLng(ride.tripDetail.destinations.get(0).lat, ride.tripDetail.destinations.get(0).lng);

                destinationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title(""));

            }

            setDriverListener();

        }
    }

    private void hideBookedMarkers() {

        //todo check how to deal with booked markers

        if (bookedPickupMarker != null) {

            bookedPickupMarker.remove();

            bookedPickupMarker = null;

        }

        if (bookedDestinationMarker != null) {

            bookedDestinationMarker.remove();

            bookedDestinationMarker = null;

        }

        if (destinationMarker2 != null)

            destinationMarker2.remove();

        destinationMarker2 = null;

        if (polyline != null)
            polyline.remove();
        if (secondDropOffPolyline != null)
            secondDropOffPolyline.remove();
    }

    Call<String> reverseCall;

    void getDirections(double originLat, double originLng, double destinationLat, double destinationLng, Boolean isSecondDropOff) {

        String url = "/maps/api/directions/json?origin=" + originLat + "," + originLng + "&destination=" + destinationLat + "," + destinationLng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DirectionsApiResponse resp = gson.fromJson(response.body(), DirectionsApiResponse.class);

                    drawPaths(resp, isSecondDropOff);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    Call<String> reverseCall1;

    void getDirectionsTowardsDropOff2(double originLat, double originLng, double destinationLat, double destinationLng, Boolean isSecondDropOff) {

        String url = "/maps/api/directions/json?origin=" + originLat + "," + originLng + "&destination=" + destinationLat + "," + destinationLng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall1 != null) {

            reverseCall1.cancel();

            reverseCall1 = null;
        }

        reverseCall1 = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall1 = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DirectionsApiResponse resp = gson.fromJson(response.body(), DirectionsApiResponse.class);

                    try {

                        drawPaths2(resp, isSecondDropOff);

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

//                    if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

                    calculateDistance2();

//                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    ArrayList<LatLng> path = new ArrayList<>();

    private void drawPaths(DirectionsApiResponse res, Boolean isSecondDropOff) {
        path = new ArrayList<>();

//        ArrayList<LatLng> path = new ArrayList<>();

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

            ex.printStackTrace();

        }

        //Draw the polyline
        if (path.size() > 0) {

            if (secondDropOffPolyline != null)

                secondDropOffPolyline.remove();

            secondDropOffPolyline = null;

            if (AppConstants.RideStatus.isRideDriverArriving(rideModel.status)) {


                //Rider is arriving show path from driver to pickup location

                if (polyline != null) {

                    polyline.remove();

                }
            }
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLACK).width(10);

            polyline = mMap.addPolyline(opts);

        }

//        if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

        calculateDistance();

//        }
    }

    private void drawPaths2(DirectionsApiResponse res, Boolean isSecondDropOff) {

//        ArrayList<LatLng> path = new ArrayList<>();

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

            ex.printStackTrace();

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

    private void checkPolyline2() {

        if (AppConstants.RideStatus.BOOKED.equalsIgnoreCase(rideModel.status) || AppConstants.RideStatus.RE_BOOKED.equalsIgnoreCase(rideModel.status)) {

            //draw polyline from dest1 to second drop off 2
            drawSecondPolyline(rideModel);

        } else if (AppConstants.RideStatus.RIDE_STARTED.equalsIgnoreCase(rideModel.status) && rideModel.tripDetail.destinations.get(0).status.equalsIgnoreCase(AppConstants.RideDetailStatus.NOT_REACHED)) {

            //ride is started handle case for driver hasn't reached any location yet draw polyline draw from dest 1 to dest 2
            //drawing polyline from dest1 to second drop off 2

            drawSecondPolyline(rideModel);

        } else {

            hideSecondPolyline();

        }

    }

    private void drawSecondPolyline(RideModel rideModel) {

        SearchedPlaceModel pickUp = rideModel.tripDetail.destinations.get(0);

        if (rideModel.tripDetail.destinations.size() == 2) {

            SearchedPlaceModel destination = rideModel.tripDetail.destinations.get(1);

            getDirectionsTowardsDropOff2(pickUp.lat, pickUp.lng, destination.lat, destination.lng, false);
        }
    }

    private void hideSecondPolyline() {

        if (secondDropOffPolyline != null) {

            secondDropOffPolyline.remove();

            secondDropOffPolyline = null;

        }
    }

    Polyline secondDropOffPolyline;

    private void setDriverListener() {

        //For Driver Location Updates

        listenerRegistration = FirebaseFirestore.getInstance().collection("Users")
                .document(rideModel.driverId)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {

                        User user = value.toObject(User.class);
                        user.id = value.getId();
                        if (rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.RIDE_COMPLETED)) {

                            //ride complete showing rating popup

                            if (ratingDialog != null && ratingDialog.isShowing()) {

                                return;
                            }

                            listenerRegistration = null;

                            ratingDialog = new RatingDialog(HomeActivity.this, rideModel, user);

                            ratingDialog.show();

                            return;

                        }

                        //Update Driver Location On Map
                        updateDriverLocation(user);

                        setUserData(user);

                        setPhoneListener(user);

                        if (vehicleDetails == null) {

                            getVehicleDetails();

                        }

                    }

                });

    }

    int distance = 0;
    int minutes = 0;

    void calculateDistance() {

        //todo destination also calculate for destination 2

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + location.getLatitude() + "," + location.getLongitude() + "&destinations=" + rideModel.tripDetail.destinations.get(0).lat + "," + rideModel.tripDetail.destinations.get(0).lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DistanceMatrixResponse resp = gson.fromJson(response.body(), DistanceMatrixResponse.class);

                    setDistance(resp);

                    checkPolyline2();

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    void calculateDistance2() {
        SearchedPlaceModel pickUp = rideModel.tripDetail.destinations.get(0);
        SearchedPlaceModel destination1 = rideModel.tripDetail.destinations.get(0);
        SearchedPlaceModel destination2 = rideModel.tripDetail.destinations.get(1);

        //todo destination also calculate for destination 2

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + destination1.lat + "," + destination1.lng + "&destinations=" + destination2.lat + "," + destination2.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DistanceMatrixResponse resp = gson.fromJson(response.body(), DistanceMatrixResponse.class);

                    setDistance2(resp);

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

                if (element.distance != null) {

                    distance = element.distance.text;
                    this.distance = element.distance.value;

                }

                if (element.duration != null) {

                    time = element.duration.text;
                    minutes = element.duration.value;

                }

            }

        }


        mBinding.timeTV.setText(time);
        mBinding.minsTV.setText(time);
        mBinding.kmTV.setText(distance);
    }

    private void setDistance2(DistanceMatrixResponse resp) {

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

                if (element.distance != null) {
                    this.distance = this.distance + element.distance.value;

                    distance = element.distance.text;

                }

                if (element.duration != null) {
                    minutes = element.duration.value + minutes;
                    time = element.duration.text;

                }

            }

        }


        float min = minutes / (60);
        float dis = this.distance / 1000;

        mBinding.timeTV.setText(String.format("%.2f", min) + " minutes");
        mBinding.minsTV.setText(String.format("%.2f", min) + " minutes");
        mBinding.kmTV.setText(String.format("%.2f", dis) + " km");

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

        if (!HomeActivity.this.isDestroyed()) {

            mBinding.numberTV.setText(vehicleDetails.tagNumber);

            mBinding.carTitleTV.setText(vehicleDetails.name);

            mBinding.colorTV.setText(vehicleDetails.make);

            Glide.with(HomeActivity.this).load(vehicleDetails.frontCarUrl).apply(new RequestOptions().centerCrop()).into(mBinding.carPic);

        }
    }

    private void setUserData(User user) {

        mBinding.nameTV.setText(user.firstName + " " + user.lastName);

        //Todo make rating hardcoded
        mBinding.ratingTV.setText("4.5");


        mBinding.msgIV.setOnClickListener(v -> startChat(user));

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

        //if already showing marker then move marker else add driver marker

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

            //moving driver marker

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


            //Rider is arriving show path from driver to pickup location

//            if (polyline != null) {

//                polyline.remove();

//            }

            getDirections(user.lat, user.lng, rideModel.tripDetail.pickUp.lat, rideModel.tripDetail.pickUp.lng, false);


        } else {

            if (rideModel.tripDetail.destinations.size() == 1) {

                //Either or ride is active and signle dropoff

                getDirections(user.lat, user.lng, rideModel.tripDetail.destinations.get(0).lat, rideModel.tripDetail.destinations.get(0).lng, false);

            } else if (rideModel.tripDetail.destinations.size() > 1) {


                if (AppConstants.RideDetailStatus.hasReached(rideModel.tripDetail.destinations.get(0).status)) {

                    //rider is moving towards destination 2

                    getDirections(user.lat, user.lng, rideModel.tripDetail.destinations.get(1).lat, rideModel.tripDetail.destinations.get(1).lng, true);


                } else {

                    //rider is moving towards destination 1 show path from destination 1 to 2

                    getDirections(user.lat, user.lng, rideModel.tripDetail.destinations.get(0).lat, rideModel.tripDetail.destinations.get(0).lng, false);

                }
            }
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

        openCloseDrawer();
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