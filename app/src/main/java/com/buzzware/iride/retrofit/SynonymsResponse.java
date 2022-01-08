package com.buzzware.iride.retrofit;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SynonymsResponse{

	@SerializedName("synonyms")
	private List<String> synonyms;

	@SerializedName("word")
	private String word;

	public List<String> getSynonyms(){
		return synonyms;
	}

	public String getWord(){
		return word;
	}
}