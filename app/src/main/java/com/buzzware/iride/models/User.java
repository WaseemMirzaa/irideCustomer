package com.buzzware.iride.models;

import java.util.ArrayList;

public class User {

    public String id;
    public String cusId;
    public String clientSecret;
    public String firstName;
    public String lastName;
    public String password;
    public String address;
    public String phoneNumber;
    public String state;
    public String city;
    public String token;

    public ArrayList<Double> ratings;

    public double lat;
    public double lng;

    public String userRole = "user";

    public String zipcode;
    public String email;
    public String image;
}
