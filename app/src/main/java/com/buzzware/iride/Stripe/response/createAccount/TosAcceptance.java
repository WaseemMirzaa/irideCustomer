package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class TosAcceptance{

	@SerializedName("date")
	private Object date;

	@SerializedName("ip")
	private Object ip;

	@SerializedName("user_agent")
	private Object userAgent;

	public Object getDate(){
		return date;
	}

	public Object getIp(){
		return ip;
	}

	public Object getUserAgent(){
		return userAgent;
	}
}