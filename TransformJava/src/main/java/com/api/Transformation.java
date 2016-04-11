/**
 * 
 */
package com.api;

import com.api.beans.NumbersInput;
import com.api.beans.Result;
import com.google.gson.Gson;

/**
 * @author sanketsw
 *
 */
public class Transformation {
	
	/**
	 * Transform the input request  data here
	 * @param jsonInput
	 * @return JSON String after conversion
	 */
	public String transformRequestData(String jsonInput) {
		NumbersInput json = new Gson().fromJson(jsonInput, NumbersInput.class);
		json.setLeft(json.getLeft() + 100);
		String out = new Gson().toJson(json);
		return out;
	}

	/**
	 * Transform the output response data here
	 * @param jsonData
	 * @return JSON String after conversion
	 */
	public String transformResponseData(String jsonData) {
		Result json = new Gson().fromJson(jsonData, Result.class);
		json.setImeplementation("IIB REST API");
		json.setComment("Transformed the output from Spring boot API");
		String out = new Gson().toJson(json);
		return out;
	}

}
