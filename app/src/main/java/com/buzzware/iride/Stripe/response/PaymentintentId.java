package com.buzzware.iride.Stripe.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentintentId{

	@SerializedName("metadata")
	private List<Object> metadata;

	@SerializedName("livemode")
	private boolean livemode;

	@SerializedName("canceled_at")
	private Object canceledAt;

	@SerializedName("amount_capturable")
	private int amountCapturable;

	@SerializedName("description")
	private Object description;

	@SerializedName("source")
	private Object source;

	@SerializedName("statement_descriptor")
	private Object statementDescriptor;

	@SerializedName("transfer_data")
	private Object transferData;

	@SerializedName("shipping")
	private Object shipping;

	@SerializedName("review")
	private Object review;

	@SerializedName("next_source_action")
	private Object nextSourceAction;

	@SerializedName("currency")
	private String currency;

	@SerializedName("id")
	private String id;

	@SerializedName("client_secret")
	private String clientSecret;

	@SerializedName("payment_method_options")
	private PaymentMethodOptions paymentMethodOptions;

	@SerializedName("payment_method")
	private Object paymentMethod;

	@SerializedName("capture_method")
	private String captureMethod;

	@SerializedName("amount")
	private int amount;

	@SerializedName("transfer_group")
	private Object transferGroup;

	@SerializedName("allowed_source_types")
	private List<String> allowedSourceTypes;

	@SerializedName("on_behalf_of")
	private Object onBehalfOf;

	@SerializedName("created")
	private int created;

	@SerializedName("payment_method_types")
	private List<String> paymentMethodTypes;

	@SerializedName("amount_received")
	private int amountReceived;

	@SerializedName("setup_future_usage")
	private Object setupFutureUsage;

	@SerializedName("confirmation_method")
	private String confirmationMethod;

	@SerializedName("cancellation_reason")
	private Object cancellationReason;

	@SerializedName("charges")
	private Charges charges;

	@SerializedName("application")
	private Object application;

	@SerializedName("receipt_email")
	private Object receiptEmail;

	@SerializedName("last_payment_error")
	private Object lastPaymentError;

	@SerializedName("next_action")
	private Object nextAction;

	@SerializedName("invoice")
	private Object invoice;

	@SerializedName("statement_descriptor_suffix")
	private Object statementDescriptorSuffix;

	@SerializedName("application_fee_amount")
	private Object applicationFeeAmount;

	@SerializedName("object")
	private String object;

	@SerializedName("customer")
	private Object customer;

	@SerializedName("status")
	private String status;

	public List<Object> getMetadata(){
		return metadata;
	}

	public boolean isLivemode(){
		return livemode;
	}

	public Object getCanceledAt(){
		return canceledAt;
	}

	public int getAmountCapturable(){
		return amountCapturable;
	}

	public Object getDescription(){
		return description;
	}

	public Object getSource(){
		return source;
	}

	public Object getStatementDescriptor(){
		return statementDescriptor;
	}

	public Object getTransferData(){
		return transferData;
	}

	public Object getShipping(){
		return shipping;
	}

	public Object getReview(){
		return review;
	}

	public Object getNextSourceAction(){
		return nextSourceAction;
	}

	public String getCurrency(){
		return currency;
	}

	public String getId(){
		return id;
	}

	public String getClientSecret(){
		return clientSecret;
	}

	public PaymentMethodOptions getPaymentMethodOptions(){
		return paymentMethodOptions;
	}

	public Object getPaymentMethod(){
		return paymentMethod;
	}

	public String getCaptureMethod(){
		return captureMethod;
	}

	public int getAmount(){
		return amount;
	}

	public Object getTransferGroup(){
		return transferGroup;
	}

	public List<String> getAllowedSourceTypes(){
		return allowedSourceTypes;
	}

	public Object getOnBehalfOf(){
		return onBehalfOf;
	}

	public int getCreated(){
		return created;
	}

	public List<String> getPaymentMethodTypes(){
		return paymentMethodTypes;
	}

	public int getAmountReceived(){
		return amountReceived;
	}

	public Object getSetupFutureUsage(){
		return setupFutureUsage;
	}

	public String getConfirmationMethod(){
		return confirmationMethod;
	}

	public Object getCancellationReason(){
		return cancellationReason;
	}

	public Charges getCharges(){
		return charges;
	}

	public Object getApplication(){
		return application;
	}

	public Object getReceiptEmail(){
		return receiptEmail;
	}

	public Object getLastPaymentError(){
		return lastPaymentError;
	}

	public Object getNextAction(){
		return nextAction;
	}

	public Object getInvoice(){
		return invoice;
	}

	public Object getStatementDescriptorSuffix(){
		return statementDescriptorSuffix;
	}

	public Object getApplicationFeeAmount(){
		return applicationFeeAmount;
	}

	public String getObject(){
		return object;
	}

	public Object getCustomer(){
		return customer;
	}

	public String getStatus(){
		return status;
	}
}