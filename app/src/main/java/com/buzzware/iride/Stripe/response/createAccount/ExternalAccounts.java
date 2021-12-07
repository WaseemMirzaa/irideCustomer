package com.buzzware.iride.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExternalAccounts{

	@SerializedName("data")
	private List<Object> data;

	@SerializedName("total_count")
	private int totalCount;

	@SerializedName("has_more")
	private boolean hasMore;

	@SerializedName("url")
	private String url;

	@SerializedName("object")
	private String object;

	public List<Object> getData(){
		return data;
	}

	public int getTotalCount(){
		return totalCount;
	}

	public boolean isHasMore(){
		return hasMore;
	}

	public String getUrl(){
		return url;
	}

	public String getObject(){
		return object;
	}
}