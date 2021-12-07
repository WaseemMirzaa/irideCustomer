package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReturnData{

	@SerializedName("tos_acceptance")
	private TosAcceptance tosAcceptance;

	@SerializedName("country")
	private String country;

	@SerializedName("settings")
	private Settings settings;

	@SerializedName("metadata")
	private List<Object> metadata;

	@SerializedName("requirements")
	private Requirements requirements;

	@SerializedName("capabilities")
	private Capabilities capabilities;

	@SerializedName("created")
	private int created;

	@SerializedName("login_links")
	private LoginLinks loginLinks;

	@SerializedName("payouts_enabled")
	private boolean payoutsEnabled;

	@SerializedName("type")
	private String type;

	@SerializedName("business_profile")
	private BusinessProfile businessProfile;

	@SerializedName("charges_enabled")
	private boolean chargesEnabled;

	@SerializedName("details_submitted")
	private boolean detailsSubmitted;

	@SerializedName("business_type")
	private Object businessType;

	@SerializedName("id")
	private String id;

	@SerializedName("default_currency")
	private String defaultCurrency;

	@SerializedName("email")
	private String email;

	@SerializedName("object")
	private String object;

	@SerializedName("external_accounts")
	private ExternalAccounts externalAccounts;

	public TosAcceptance getTosAcceptance(){
		return tosAcceptance;
	}

	public String getCountry(){
		return country;
	}

	public Settings getSettings(){
		return settings;
	}

	public List<Object> getMetadata(){
		return metadata;
	}

	public Requirements getRequirements(){
		return requirements;
	}

	public Capabilities getCapabilities(){
		return capabilities;
	}

	public int getCreated(){
		return created;
	}

	public LoginLinks getLoginLinks(){
		return loginLinks;
	}

	public boolean isPayoutsEnabled(){
		return payoutsEnabled;
	}

	public String getType(){
		return type;
	}

	public BusinessProfile getBusinessProfile(){
		return businessProfile;
	}

	public boolean isChargesEnabled(){
		return chargesEnabled;
	}

	public boolean isDetailsSubmitted(){
		return detailsSubmitted;
	}

	public Object getBusinessType(){
		return businessType;
	}

	public String getId(){
		return id;
	}

	public String getDefaultCurrency(){
		return defaultCurrency;
	}

	public String getEmail(){
		return email;
	}

	public String getObject(){
		return object;
	}

	public ExternalAccounts getExternalAccounts(){
		return externalAccounts;
	}
}