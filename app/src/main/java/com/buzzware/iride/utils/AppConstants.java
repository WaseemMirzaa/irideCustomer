package com.buzzware.iride.utils;

public class AppConstants {

    public static final String GOOGLE_PLACES_API_KEY = "AIzaSyAKhmWBB8YzvlrFUGPBt5pluE4pN3RFNHk";

    public static class RideStatus {

        public static final String BOOKED = "booked";
        public static final String DRIVER_ACCEPTED = "driverAccepted";
        public static final String DRIVER_REACHED = "driverReached";
        public static final String RIDE_STARTED = "rideStarted";
        public static final String RIDE_COMPLETED = "rideCompleted";
        public static final String RATED = "rated";
        public static final String CANCELLED = "cancelled";

        public static boolean isRideInProgress(String status) {

            return status.equalsIgnoreCase(RIDE_STARTED);

        }

        public static boolean isRideDriverArriving(String status) {

            return status.equalsIgnoreCase(DRIVER_ACCEPTED) || status.equalsIgnoreCase(DRIVER_REACHED);

        }
    }

    public static class RideDetailStatus {

        public static final String REACHED = "1";
        public static final String NOT_REACHED = "0";

        public static boolean hasReached(String status) {

            return status.equalsIgnoreCase(REACHED);

        }

    }

    public static class RideTypeStatus {

        public static final String REACHED = "1";
        public static final String NOT_REACHED = "0";

        public static boolean hasReached(String status) {

            return status.equalsIgnoreCase(REACHED);

        }

    }
}
