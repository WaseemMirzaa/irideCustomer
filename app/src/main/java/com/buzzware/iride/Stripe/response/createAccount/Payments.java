package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class Payments{

	@SerializedName("statement_descriptor")
	private Object statementDescriptor;

	@SerializedName("statement_descriptor_kanji")
	private Object statementDescriptorKanji;

	@SerializedName("statement_descriptor_kana")
	private Object statementDescriptorKana;

	public Object getStatementDescriptor(){
		return statementDescriptor;
	}

	public Object getStatementDescriptorKanji(){
		return statementDescriptorKanji;
	}

	public Object getStatementDescriptorKana(){
		return statementDescriptorKana;
	}
}