package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class CreateAccountResponse{

	@SerializedName("return_data")
	private ReturnData returnData;

	public ReturnData getReturnData(){
		return returnData;
	}
}