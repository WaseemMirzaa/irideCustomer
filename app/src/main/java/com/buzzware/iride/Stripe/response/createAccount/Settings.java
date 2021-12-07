package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Settings{

	@SerializedName("payouts")
	private Payouts payouts;

	@SerializedName("card_issuing")
	private CardIssuing cardIssuing;

	@SerializedName("branding")
	private Branding branding;

	@SerializedName("payments")
	private Payments payments;

	@SerializedName("sepa_debit_payments")
	private List<Object> sepaDebitPayments;

	@SerializedName("bacs_debit_payments")
	private List<Object> bacsDebitPayments;

	@SerializedName("card_payments")
	private CardPayments cardPayments;

	@SerializedName("dashboard")
	private Dashboard dashboard;

	public Payouts getPayouts(){
		return payouts;
	}

	public CardIssuing getCardIssuing(){
		return cardIssuing;
	}

	public Branding getBranding(){
		return branding;
	}

	public Payments getPayments(){
		return payments;
	}

	public List<Object> getSepaDebitPayments(){
		return sepaDebitPayments;
	}

	public List<Object> getBacsDebitPayments(){
		return bacsDebitPayments;
	}

	public CardPayments getCardPayments(){
		return cardPayments;
	}

	public Dashboard getDashboard(){
		return dashboard;
	}
}