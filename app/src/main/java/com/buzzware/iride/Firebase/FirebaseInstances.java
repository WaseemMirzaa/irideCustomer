package com.buzzware.iride.Firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseInstances {

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance().gete;

    public static CollectionReference bookingsCollection = firebaseFirestore.collection("Bookings");
    public static CollectionReference scheduledRidesCollection = firebaseFirestore.collection("ScheduledRides");

}
