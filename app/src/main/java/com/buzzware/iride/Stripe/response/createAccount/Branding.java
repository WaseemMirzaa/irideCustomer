package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class Branding{

	@SerializedName("icon")
	private Object icon;

	@SerializedName("logo")
	private Object logo;

	@SerializedName("secondary_color")
	private Object secondaryColor;

	@SerializedName("primary_color")
	private Object primaryColor;

	public Object getIcon(){
		return icon;
	}

	public Object getLogo(){
		return logo;
	}

	public Object getSecondaryColor(){
		return secondaryColor;
	}

	public Object getPrimaryColor(){
		return primaryColor;
	}
}