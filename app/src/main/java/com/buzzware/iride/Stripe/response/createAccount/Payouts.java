package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class Payouts{

	@SerializedName("debit_negative_balances")
	private boolean debitNegativeBalances;

	@SerializedName("statement_descriptor")
	private Object statementDescriptor;

	@SerializedName("schedule")
	private Schedule schedule;

	public boolean isDebitNegativeBalances(){
		return debitNegativeBalances;
	}

	public Object getStatementDescriptor(){
		return statementDescriptor;
	}

	public Schedule getSchedule(){
		return schedule;
	}
}