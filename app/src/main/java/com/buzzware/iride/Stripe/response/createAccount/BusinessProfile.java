package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class BusinessProfile{

	@SerializedName("support_email")
	private Object supportEmail;

	@SerializedName("support_url")
	private Object supportUrl;

	@SerializedName("support_address")
	private Object supportAddress;

	@SerializedName("support_phone")
	private Object supportPhone;

	@SerializedName("name")
	private Object name;

	@SerializedName("mcc")
	private Object mcc;

	@SerializedName("product_description")
	private Object productDescription;

	@SerializedName("url")
	private Object url;

	public Object getSupportEmail(){
		return supportEmail;
	}

	public Object getSupportUrl(){
		return supportUrl;
	}

	public Object getSupportAddress(){
		return supportAddress;
	}

	public Object getSupportPhone(){
		return supportPhone;
	}

	public Object getName(){
		return name;
	}

	public Object getMcc(){
		return mcc;
	}

	public Object getProductDescription(){
		return productDescription;
	}

	public Object getUrl(){
		return url;
	}
}