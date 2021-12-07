package com.buzzware.iride.Stripe.response.LinkAccount;

import com.google.gson.annotations.SerializedName;

public class ReturnData{

	@SerializedName("expires_at")
	private int expiresAt;

	@SerializedName("created")
	private int created;

	@SerializedName("url")
	private String url;

	@SerializedName("object")
	private String object;

	public int getExpiresAt(){
		return expiresAt;
	}

	public int getCreated(){
		return created;
	}

	public String getUrl(){
		return url;
	}

	public String getObject(){
		return object;
	}
}