package com.buzzware.iride.Stripe;

public class StartPayment {

    public int position;

    public String id;

    public StartPayment(int position, String ordersId) {
        this.position = position;
        id = ordersId;
    }
}
