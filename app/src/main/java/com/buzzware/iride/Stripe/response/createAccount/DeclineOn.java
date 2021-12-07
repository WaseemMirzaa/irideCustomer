package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class DeclineOn{

	@SerializedName("avs_failure")
	private boolean avsFailure;

	@SerializedName("cvc_failure")
	private boolean cvcFailure;

	public boolean isAvsFailure(){
		return avsFailure;
	}

	public boolean isCvcFailure(){
		return cvcFailure;
	}
}