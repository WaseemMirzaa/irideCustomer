package com.buzzware.iride.Stripe.response;

import com.google.gson.annotations.SerializedName;

public class StripeResponse{

	@SerializedName("paymentintent_id")
	private PaymentintentId paymentintentId;

	public PaymentintentId getPaymentintentId(){
		return paymentintentId;
	}
}