package com.buzzware.iride.screens;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.buzzware.iride.R;
import com.buzzware.iride.adapters.RideType;
import com.buzzware.iride.adapters.ScheduledRidesAdapter;
import com.buzzware.iride.adapters.UpcomingRidesAdapter;
import com.buzzware.iride.databinding.FragmentBookingsBinding;
import com.buzzware.iride.models.RideModel;
import com.buzzware.iride.models.ScheduleModel;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class BookingsActivity extends BaseNavDrawer implements View.OnClickListener {

    FragmentBookingsBinding mBinding;

//    BookingPagerAdapter bookingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = FragmentBookingsBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        Init();

        selectedDate = -1;

        mBinding.drawerIcon.setOnClickListener(v -> openCloseDrawer());

        setBaseListeners();

        setListener();

        SetupTabView(0);
//        setListeners();
    }

    private void setListener() {


        mBinding.calendarCV.setOnDayClickListener(eventDay -> {

            mBinding.calendarCV.setSelectedDates(new ArrayList<>());

            try {

                mBinding.calendarCV.setDate(eventDay.getCalendar().getTime());

            } catch (OutOfDateRangeException e) {

                e.printStackTrace();

            }

            mBinding.calendarCV.setSelected(true);

            selectedDate = new Date().getTime();

            getRides();
        });
    }


    private void Init() {

        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText("SCHEDULED"));

        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText("History"));
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText("Upcoming"));

        mBinding.tabLayout.addOnTabSelectedListener(onTabSelectedListener);

//        mBinding.firstTabLay.setOnClickListener(v -> SetupTabView(0));
//        mBinding.secondTabLay.setOnClickListener(v -> SetupTabView(1));
    }


    TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {

            SetupTabView(tab.getPosition());

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    public void SetupTabView(int position) {
        if (position == 1) //history tab
        {
            rideType = RideType.completed;

            mBinding.calendarCV.setVisibility(View.GONE);


        } else if (position == 2){ /// upcomming tab

            rideType = RideType.upcoming;

            mBinding.calendarCV.setVisibility(View.GONE);

        } else {

            mBinding.calendarCV.setVisibility(View.VISIBLE);

            rideType = RideType.scheduled;

            selectedDate = -1;
        }

        rides = new ArrayList<>();

        setAdapter(rides);

        showLoader();
        getRides();

    }

    RideType rideType = RideType.completed;

    private void getRides() {

        Query query;

        if (rideType == RideType.upcoming) {

            query = FirebaseFirestore.getInstance().collection("Bookings")
                    .whereEqualTo("userId", getUserId())
                    .whereIn("status", Arrays.asList("booked", "driverAccepted", "driverReached", "rideStarted"));

        } else if(rideType == RideType.completed){

            query = FirebaseFirestore.getInstance().collection("Bookings")
                    .whereEqualTo("userId", getUserId())
                    .whereIn("status", Arrays.asList("rated", "rideCompleted"));

        } else {


            query = FirebaseFirestore.getInstance().collection("ScheduledRides")
                    .whereEqualTo("userId", getUserId());

        }
        query.get()
                .addOnCompleteListener(
                        this::parseSnapshot
                );

    }

    ArrayList<RideModel> rides;

    Query query;

    ArrayList<ScheduleModel> scheduledRides;

    void parseSchedules(Task<QuerySnapshot> task) {

//        query = null;

        hideLoader();

        ArrayList<Calendar> dateArrayList = new ArrayList<>();

        scheduledRides = new ArrayList<>();

        if (task.isSuccessful()) {

            for (DocumentSnapshot document : task.getResult().getDocuments()) {

                ScheduleModel scheduleModel = document.toObject(ScheduleModel.class);

                scheduleModel.id = document.getId();

//                double distanceInMiles = distance(scheduleModel.tripDetail.pickUp.lat, scheduleModel.tripDetail.pickUp.lng, location.getLatitude(), location.getLongitude());

                if (selectedDate < 0) {

                    Calendar calendar = Calendar.getInstance();

                    Date date = new Date();

                    date.setTime(scheduleModel.scheduleTimeStamp);

                    calendar.setTime(date);

                    dateArrayList.add(calendar);

                    scheduledRides.add(scheduleModel);

                } else {
//
                    for (Calendar c : mBinding.calendarCV.getSelectedDates()) {

                        Date bookingDate = new Date();

                        bookingDate.setTime(scheduleModel.bookingDate);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");

                        String date = simpleDateFormat.format(c.getTime());
                        String date1 = simpleDateFormat1.format(bookingDate);

                        if (date.equalsIgnoreCase(date1)) {

                            scheduledRides.add(scheduleModel);

                        }
                    }
                }

                if (selectedDate < 0) {

                    mBinding.calendarCV.setSelectedDates(dateArrayList);

                }

            }

            selectedDate = new Date().getTime();

        }

        setScheduleAdapter(scheduledRides);

    }

    long selectedDate = -1;

    private void setScheduleAdapter(ArrayList<ScheduleModel> rides) {

        mBinding.ridesRV.setLayoutManager(new LinearLayoutManager(BookingsActivity.this));

        mBinding.ridesRV.setAdapter(new ScheduledRidesAdapter(BookingsActivity.this,
                rides));

    }
    void parseSnapshot(Task<QuerySnapshot> task) {

        if(rideType == RideType.scheduled) {

            parseSchedules(task);

            return;

        }
        hideLoader();

        rides = new ArrayList<>();

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                RideModel rideModel = document.toObject(RideModel.class);

                rideModel.id = document.getId();

                rides.add(rideModel);

            }

        }

        setAdapter(rides);

    }

    private void setAdapter(ArrayList<RideModel> rides) {

        mBinding.ridesRV.setLayoutManager(new LinearLayoutManager(BookingsActivity.this));

        mBinding.ridesRV.setAdapter(new UpcomingRidesAdapter(BookingsActivity.this,
                rides,
                new UpcomingRidesAdapter.UpcomingRideActionListener() {
                    @Override
                    public void acceptRide(RideModel rideModel) {

//                        accept(rideModel);

                    }

                    @Override
                    public void moveToCompleteScreen(RideModel rideModel) {

//                        moveToOnTrip(rideModel);

                    }
                },
                RideType.completed));

    }
}