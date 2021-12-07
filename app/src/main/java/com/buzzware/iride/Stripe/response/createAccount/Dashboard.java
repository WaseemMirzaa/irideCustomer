package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class Dashboard{

	@SerializedName("timezone")
	private String timezone;

	@SerializedName("display_name")
	private Object displayName;

	public String getTimezone(){
		return timezone;
	}

	public Object getDisplayName(){
		return displayName;
	}
}