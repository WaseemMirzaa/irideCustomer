package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class CardPayments{

	@SerializedName("statement_descriptor_prefix")
	private Object statementDescriptorPrefix;

	@SerializedName("decline_on")
	private DeclineOn declineOn;

	public Object getStatementDescriptorPrefix(){
		return statementDescriptorPrefix;
	}

	public DeclineOn getDeclineOn(){
		return declineOn;
	}
}