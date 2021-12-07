package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class CardIssuing{

	@SerializedName("tos_acceptance")
	private TosAcceptance tosAcceptance;

	public TosAcceptance getTosAcceptance(){
		return tosAcceptance;
	}
}