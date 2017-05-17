package com.myretail.domain;

import org.springframework.data.mongodb.core.mapping.Document;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

@Document(collection = "PRICING")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PricingDTO {

	@Id
	private String id;

	@JsonIgnore
	private String productId;

	@JsonProperty("value")
	private double currentPrice;

	@JsonProperty("currency_code")
	private String currencyCode;

	public PricingDTO() {
	}

	public PricingDTO(String productId, double currentPrice, String currencyCode) {
		this.productId = productId;
		this.currentPrice = currentPrice;
		this.currencyCode = currencyCode;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyName) {
		this.currencyCode = currencyName;
	}

}
