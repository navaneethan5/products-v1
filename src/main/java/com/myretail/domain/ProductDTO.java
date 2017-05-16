package com.myretail.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {

	@JsonProperty("name")
	public String productName;

	@JsonProperty("id")
	public String productId;

	@JsonProperty("current_price")
	public PricingDTO pricingDTO;

	public ProductDTO() {
	}

	public ProductDTO(String title, String tcin, PricingDTO pricingDTO) {
		this.productName = title;
		this.productId = tcin;
		this.pricingDTO = pricingDTO;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public PricingDTO getPricingDTO() {
		return pricingDTO;
	}

	public void setPricingDTO(PricingDTO productDTO) {
		this.pricingDTO = productDTO;
	}

}
