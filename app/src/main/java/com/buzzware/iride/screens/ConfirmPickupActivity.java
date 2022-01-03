package com.buzzware.iride.screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.buzzware.iride.R;
import com.buzzware.iride.Stripe.MyEphemeralKeyProvider;
import com.buzzware.iride.Stripe.PaymentResultCallback;
import com.buzzware.iride.databinding.AddCardDialogBinding;
import com.buzzware.iride.databinding.FragmentConfirmPickupBinding;
import com.buzzware.iride.databinding.PaymentDialogBinding;
import com.buzzware.iride.models.RideModel;
import com.buzzware.iride.models.ScheduleModel;
import com.buzzware.iride.models.SearchedPlaceModel;
import com.buzzware.iride.models.TripDetail;
import com.buzzware.iride.models.User;
import com.buzzware.iride.models.settings.Price;
import com.buzzware.iride.models.settings.Prices;
import com.buzzware.iride.response.directions.DirectionsApiResponse;
import com.buzzware.iride.response.directions.Leg;
import com.buzzware.iride.response.directions.Route;
import com.buzzware.iride.response.directions.Step;
import com.buzzware.iride.response.distanceMatrix.DistanceMatrixResponse;
import com.buzzware.iride.response.distanceMatrix.Element;
import com.buzzware.iride.response.distanceMatrix.Row;
import com.buzzware.iride.retrofit.Controller;
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
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.buzzware.iride.retrofit.Controller.Base_Url;
import static com.buzzware.iride.retrofit.Controller.Base_Url_Payments;


public class ConfirmPickupActivity extends BaseNavDrawer implements OnMapReadyCallback, View.OnClickListener {

    enum CurrentSelection {

        NORMAL_RIDE,
        SCHEDULED_RIDE

    }

    CurrentSelection currentSelection;

    FragmentConfirmPickupBinding mBinding;

    GoogleMap mMap;

    SearchedPlaceModel pickUpLocation, destinationLocation, secondDropOff;

    double amount = 0;

    Stripe stripe;

    PaymentSession paymentSession;

    Boolean readyToCharge = false;

    String clientSecret;
    String customerId;
    String orderClientSecret;

    double price;

    private int mYear, mMonth, mDay, mHour, mMinute;

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

    double distance;

    private void getTotalDistanceTillPoint1() {

        distance = 0;
        min = 0;

        showLoader();
        //todo destination also calculate for destination 2

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + pickUpLocation.lat + "," + pickUpLocation.lng + "&destinations=" + destinationLocation.lat + "," + destinationLocation.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

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

                    if (secondDropOff != null) {

                        getDistanceTillSecondDropOff();

                    } else {

                        getPrices();

                    }

                } else {

                    hideLoader();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
                hideLoader();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        hideLoader();

    }

    double min = 0;

    private void getDistanceTillSecondDropOff() {

        //todo destination also calculate for destination 2

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + destinationLocation.lat + "," + destinationLocation.lng + "&destinations=" + secondDropOff.lat + "," + secondDropOff.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

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

                    getPrices();

                } else
                    hideLoader();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
                hideLoader();
            }
        });
    }

    double iRideLuxPrice = 0;
    double iRidePlusPrice = 0;
    double iRidePrice = 0;

    private void getPrices() {

        FirebaseFirestore.getInstance().collection("Settings")
                .document("Prices")
                .get()
                .addOnCompleteListener(task -> {
                    hideLoader();
                    if (task.isSuccessful()) {

                        Prices settings = task.getResult().toObject(Prices.class);

                        distance = convertKmsToMiles(distance / 1000);

                        min = min / 60;
                        calculateLuxPrice(settings);

                        calculateIRidePrice(settings);

                        calculatePlusPrice(settings);

                        mBinding.estimateTV.setText("$" + String.format("%.2f", iRidePrice));

                        amount = iRidePrice;

                        getDirections();

                    } else {

                        if (task.getException() != null && task.getException().getLocalizedMessage() != null)

                            showErrorAlert(task.getException().getLocalizedMessage());
                    }

                });

    }

    private void calculateLuxPrice(Prices settings) {

        Price price = settings.iRideLux;

        double total = price.getInitialFee() + (price.getPricePerMile() * distance) + (price.getPricePerMin() * min) + price.getCostOfVehicle();

        iRideLuxPrice = total;

    }

    private void calculateIRidePrice(Prices settings) {

        Price price = settings.iRide;

        double total = price.getInitialFee() + (price.getPricePerMile() * distance) + (price.getPricePerMin() * min) + price.getCostOfVehicle();

        iRidePrice = total;

    }

    private void calculatePlusPrice(Prices settings) {

        Price price = settings.iRidePlus;

        double total = price.getInitialFee() + (price.getPricePerMile() * distance) + (price.getPricePerMin() * min) + price.getCostOfVehicle();

        iRidePlusPrice = total;
    }

    private void setDistance(DistanceMatrixResponse resp) {

        if (resp.rows != null && resp.rows.size() > 0) {

            Row row = resp.rows.get(0);

            if (row.elements != null && row.elements.size() > 0) {

                Element element = row.elements.get(0);

                if (element.distance != null) {

                    distance = distance + element.distance.value;

                }

                if (element.duration != null) {

                    min = element.duration.value;

                }
            }

        }
    }

    public double convertKmsToMiles(double kms) {
        double miles = 0.621371 * kms;
        return miles;
    }

    String dateString = "";
    String timeString = "";

    void showDatePicker() {

        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    dateString = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                    showTimePicker();

                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    void showTimePicker() {

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {

                    timeString = hourOfDay + ":" + minute;

                    try {

                        validateDateTime();

                    } catch (ParseException e) {

                        e.printStackTrace();

                    }

                }, mHour, mMinute, false);

        timePickerDialog.show();

    }

    long time = -1;

    private void validateDateTime() throws ParseException {

        String tsString = dateString + " " + timeString;

        String inputFormat = "dd-MM-yyyy hh:mm";

        Date date = new SimpleDateFormat(inputFormat).parse(tsString);

        Date currentDate = new Date();

        this.time = date.getTime();

        if (date.getTime() < currentDate.getTime()) {

            dateString = "";

            timeString = "";

            showErrorAlert("Invalid date time entered. Please select future date and time.");

            return;

        }

        getScheduledRides(time);
    }

    private void setListeners() {

        mBinding.btnConfirmPickup.setOnClickListener(this);

        mBinding.pricingRG.setOnCheckedChangeListener(this::onCheckedChanged);

        mBinding.backIcon.setOnClickListener(v -> finish());

        mBinding.scheduleRideRL.setOnClickListener(v -> setCurrentSelectionAndPay(CurrentSelection.SCHEDULED_RIDE));
    }

    void setCurrentSelectionAndPay(CurrentSelection selection) {

        currentSelection = selection;

        if (selection == CurrentSelection.NORMAL_RIDE) {

            getScheduledRides(new Date().getTime());

        } else {

            showDatePicker();

        }
    }

    @SuppressLint("SetTextI18n")
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (checkedId == mBinding.iRideRB.getId()) {

            mBinding.estimateTV.setText("$" + String.format("%.2f", iRidePrice));

            amount = iRidePrice;

        } else if (checkedId == mBinding.luxRB.getId()) {

            mBinding.estimateTV.setText("$" + String.format("%.2f", iRideLuxPrice));

            amount = iRideLuxPrice;

        } else {

            mBinding.estimateTV.setText("$" + String.format("%.2f", iRidePlusPrice));

            amount = iRidePlusPrice;

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

        if (secondDropOff != null) {

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

        getTotalDistanceTillPoint1();
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

        reverseCall = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

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

            setCurrentSelectionAndPay(CurrentSelection.NORMAL_RIDE);
        }
    }

    void getActiveRide() {

        Query query = FirebaseFirestore.getInstance().collection("Bookings")
                .whereEqualTo("userId", getUserId())
                .whereIn("status", Arrays.asList("driverAccepted", "driverReached", "rideStarted", "booked", AppConstants.RideStatus.RE_BOOKED, AppConstants.RideStatus.RIDE_COMPLETED));

        query.get()
                .addOnCompleteListener(
                        this::parseSnapshot
                );
    }

    void getScheduledRides(long time) {

        showLoader();

        Query query = FirebaseFirestore.getInstance().collection("ScheduledRides")
                .whereEqualTo("userId", getUserId());

        query.get()
                .addOnCompleteListener(task -> parseSnapshot(task, time));
    }

    void parseSnapshot(Task<QuerySnapshot> task, long time) {

        hideKeyboard();

        Boolean shouldSchedule = true;

        hideLoader();

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                ScheduleModel rideModel = document.toObject(ScheduleModel.class);

                rideModel.id = document.getId();


                long durationLeft = rideModel.scheduleTimeStamp - time;

                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(durationLeft);

                if (diffInMinutes < 30 && diffInMinutes > -30) {

                    shouldSchedule = false;

                }
            }

        }

        if (!shouldSchedule) {

            showErrorAlert("Already have a ride scheduled within this timeframe");

        } else {

            if (currentSelection == CurrentSelection.SCHEDULED_RIDE)

                getCurrentUserData();
            else
                getActiveRide();


        }

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

            getCurrentUserData();

        }

    }

    void scheduleRide(long time) {

        Map<String, Object> map = new HashMap<>();

        map.put("bookingDate", time);

        TripDetail tripDetail = new TripDetail();

        tripDetail.destinations = new ArrayList<>();

        tripDetail.destinations.add(destinationLocation);

        if (secondDropOff != null) {

            tripDetail.destinations.add(secondDropOff);

        }

        String id = getAlphaNumericString(15);

        tripDetail.pickUp = pickUpLocation;

        map.put("tripDetail", tripDetail);
        map.put("scheduledDate", dateString);
        map.put("scheduledTime", timeString);
        map.put("scheduleTimeStamp", time);
        map.put("id", id);
        map.put("userId", getUserId());
        map.put("price", "" + amount);
        map.put("status", "booked");

        FirebaseFirestore.getInstance().collection("ScheduledRides")
                .document(id).set(map);

        Toast.makeText(this, "Successfully Scheduled", Toast.LENGTH_LONG).show();

        startActivity(new Intent(this, BookARideActivity.class)
                .addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
        );

        finish();

    }

    static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private void placeOrder() {

        RideModel rideModel = new RideModel();

        rideModel.id = getAlphaNumericString(15);

        rideModel.bookingDate = new Date().getTime();

        rideModel.tripDetail = new TripDetail();

        rideModel.tripDetail.destinations = new ArrayList<>();

        rideModel.tripDetail.destinations.add(destinationLocation);

        if (secondDropOff != null) {

            rideModel.tripDetail.destinations.add(secondDropOff);

        }

        rideModel.tripDetail.pickUp = pickUpLocation;

        rideModel.userId = getUserId();

        rideModel.price = "" + amount;

        rideModel.status = "booked";

        FirebaseFirestore.getInstance().collection("Bookings")
                .document(rideModel.id).set(rideModel);

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

    //Stripe

    void getCurrentUserData() {

        FirebaseFirestore.getInstance().collection("Users")
                .document(getUserId())
                .get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                User user = task.getResult().toObject(User.class);

                if (user != null) {

                    if (user.cusId != null && user.clientSecret != null) {

                        customerId = user.cusId;
                        clientSecret = user.clientSecret;
                        MyEphemeralKeyProvider.cusId = customerId;

                        setUpStripe();

                    } else {

                        createCustomer(user);
                    }

                }
            }

        });

    }

    private void createCustomer(User user) {

        MyEphemeralKeyProvider.cusId = "1";

        showLoader();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", user.firstName + user.lastName)
                .addFormDataPart("email", user.email)
                .addFormDataPart("cus_id", "")
                .build();

        Controller.getApi(Base_Url_Payments).createCustomer(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        hideLoader();

                        if (response.body() != null) {

                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                customerId = jsonObject.getString("cus_id");
                                String clientSecret = jsonObject.getString("key");

                                MyEphemeralKeyProvider.cusId = customerId;

                                user.clientSecret = clientSecret;
                                user.cusId = customerId;


                                FirebaseFirestore.getInstance().collection("Users")
                                        .document(getUserId())
                                        .set(user).addOnCompleteListener(task -> {

                                    setUpStripe();

                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });
    }

    private void setUpStripe() {

        PaymentConfiguration.init(
                ConfirmPickupActivity.this,
                AppConstants.stripeKey
        );

        stripe = new Stripe(ConfirmPickupActivity.this, AppConstants.stripeKey);

        CustomerSession.initCustomerSession(
                ConfirmPickupActivity.this,
                new MyEphemeralKeyProvider()
        );
        paymentSession = new PaymentSession(
                ConfirmPickupActivity.this,
                new PaymentSessionConfig.Builder()
                        .setShippingInfoRequired(false)
                        .setShippingMethodsRequired(false)
                        .setShippingInfoRequired(false)
                        .build()
        );
        paymentSession.init(
                new PaymentSession.PaymentSessionListener() {
                    @Override
                    public void onCommunicatingStateChanged(
                            boolean isCommunicating
                    ) {


                        if (isCommunicating)
                            hideLoader();
                        else
                            showLoader();
                        Log.d("TAG", "onCommunicatingStateChanged: " + isCommunicating);
                    }

                    @Override
                    public void onError(
                            int errorCode,
                            @Nullable String errorMessage
                    ) {

                        showErrorAlert(errorMessage);

                    }

                    @Override
                    public void onPaymentSessionDataChanged(
                            @NonNull PaymentSessionData data
                    ) {
                        final PaymentMethod paymentMethod = data.getPaymentMethod();

                        readyToCharge = false;


                        if (data.isPaymentReadyToCharge()) {

                            showLoader();

                            readyToCharge = true;

                            callPaymentApi(paymentMethod);
                        } else {

                            hideLoader();

                            paymentSession.presentPaymentMethodSelection(MyEphemeralKeyProvider.cusId);

                            if (paymentMethod != null) {

                                Log.d("Order", "Ready: ");

                            }
                        }

                    }
                }
        );
    }

    private void callPaymentApi(PaymentMethod paymentMethod) {

        Toast.makeText(ConfirmPickupActivity.this, "Ready to charge", Toast.LENGTH_SHORT).show();

        showLoader();

        RequestBody requestBody;
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("amount", ((int) new Double(amount).intValue()) * 100 + "")//stripeval
                .addFormDataPart("pm_id", paymentMethod.id)
                .addFormDataPart("cus_id", customerId)
                .build();

        Controller.getApi(Base_Url_Payments).accountPayment(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

//                        hideLoader();

                        if (response.body() != null) {

                            try {

                                JSONObject jsonObject = new JSONObject(response.body());

                                orderClientSecret = jsonObject.getJSONObject("return_data").getString("key");

                                if (jsonObject.getJSONObject("return_data").getInt("error") == 0) {

                                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                            .create(orderClientSecret);

                                    stripe = new Stripe(
                                            ConfirmPickupActivity.this,
                                            AppConstants.stripeKey
                                    );

                                    stripe.confirmPayment((Activity) ConfirmPickupActivity.this, confirmParams);

                                } else {

                                    hideLoader();

                                    showErrorAlert("Transaction failed, please contact service provider or admin!");

                                }

                            } catch (JSONException e) {

                                hideLoader();

                                e.printStackTrace();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        hideLoader();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && !readyToCharge) {

            paymentSession.handlePaymentData(requestCode, resultCode, data);

        }

        if (readyToCharge) {

            showLoader();

            stripe.onPaymentResult(requestCode,
                    data,
                    new PaymentResultCallback(ConfirmPickupActivity.this));

        }
    }

    public void showSuccessMessage() {

        if (currentSelection == CurrentSelection.NORMAL_RIDE) {

            placeOrder();

        } else {

            scheduleRide(time);

        }

    }

}