package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class Capabilities{

	@SerializedName("transfers")
	private String transfers;

	@SerializedName("card_payments")
	private String cardPayments;

	public String getTransfers(){
		return transfers;
	}

	public String getCardPayments(){
		return cardPayments;
	}
}