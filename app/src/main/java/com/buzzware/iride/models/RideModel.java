package com.buzzware.iride.models;

public class RideModel {

    public String id;

    public String userId;

    public String driverId;

    public String vehicleId;

    public long bookingDate;

    public Double driverLat;

    public Double driverLng;

    public String status;

    public String price;

    public SearchedPlaceModel pickUp;

    public SearchedPlaceModel destination;

    public RideModel() {

    }

    public RideModel(String userId, String driverId, String vehicleId, long bookingDate, Double driverLat, Double driverLng, String status) {
        this.userId = userId;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.bookingDate = bookingDate;
        this.driverLat = driverLat;
        this.driverLng = driverLng;
        this.status = status;
    }

}
