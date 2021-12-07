package com.buzzware.iride.Stripe.response;

import com.google.gson.annotations.SerializedName;

public class Card{

	@SerializedName("installments")
	private Object installments;

	@SerializedName("request_three_d_secure")
	private String requestThreeDSecure;

	@SerializedName("network")
	private Object network;

	public Object getInstallments(){
		return installments;
	}

	public String getRequestThreeDSecure(){
		return requestThreeDSecure;
	}

	public Object getNetwork(){
		return network;
	}
}