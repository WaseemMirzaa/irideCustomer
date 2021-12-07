package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Requirements{

	@SerializedName("currently_due")
	private List<String> currentlyDue;

	@SerializedName("current_deadline")
	private Object currentDeadline;

	@SerializedName("eventually_due")
	private List<String> eventuallyDue;

	@SerializedName("past_due")
	private List<String> pastDue;

	@SerializedName("disabled_reason")
	private String disabledReason;

	@SerializedName("errors")
	private List<Object> errors;

	@SerializedName("pending_verification")
	private List<Object> pendingVerification;

	public List<String> getCurrentlyDue(){
		return currentlyDue;
	}

	public Object getCurrentDeadline(){
		return currentDeadline;
	}

	public List<String> getEventuallyDue(){
		return eventuallyDue;
	}

	public List<String> getPastDue(){
		return pastDue;
	}

	public String getDisabledReason(){
		return disabledReason;
	}

	public List<Object> getErrors(){
		return errors;
	}

	public List<Object> getPendingVerification(){
		return pendingVerification;
	}
}