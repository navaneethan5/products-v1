package com.myretail.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myretail.constants.ProductConstants;
import com.myretail.domain.ProductDTO;
import com.myretail.service.ProductService;
import com.myretail.util.Message;

@RestController
@RequestMapping("/products/v1")
public class ProductController {

	ProductService productService;

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	/**
	 * Request to get product summary along with the pricing.This will call
	 * external API to get product detail and call database to retrieve pricing
	 * information
	 * 
	 * @param productId
	 * @return
	 */
	@RequestMapping(value = "/{productId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProductPricingDetails(@PathVariable String productId, @RequestParam("key") String key)
			throws JsonProcessingException, IOException {

		log.info("Calling REST method getProductPricingDetails");

		ProductDTO productPricingInfo = null;
		Message message = new Message();

		if (!key.equals(ProductConstants.CONSUMER_KEY)) {
			log.info("Invalid consumer key");
			message.setError("Unauthorized");
			message.setMessage("Invalid Key");
			return new ResponseEntity<Message>(message, HttpStatus.UNAUTHORIZED);
		}

		productPricingInfo = (ProductDTO) productService.getProductDetails(productId);
		if (null == productPricingInfo.getProductName()) {
			log.info("Product name could not be found for the product id " + productId);
			message.setError("Product name not found");
			message.setMessage("Product name could not be found for the given product id");
			return new ResponseEntity<Message>(message, HttpStatus.NOT_FOUND);
		} else if (null == productPricingInfo.getPricingDTO()) {
			log.info("Pricing details could not be found for the product id " + productId);
			message.setError("Pricing details not found");
			message.setMessage("Pricing details could not be found for the given product id");
			return new ResponseEntity<Message>(message, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<ProductDTO>(productPricingInfo, HttpStatus.OK);
	}

	/**
	 * This method updates the pricing information for a given productId. This
	 * request required HTTP request to have Content-Type as application/json
	 * 
	 * @param productId
	 * @param productDTO
	 * @return
	 */
	@RequestMapping(value = "/{productId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updatePricingDetails(@PathVariable String productId, @RequestBody ProductDTO productDTO,
			@RequestParam("key") String key) {

		log.info("Calling REST method updatePricingDetails");

		Message message = new Message();
		if (!key.equals(ProductConstants.CONSUMER_KEY)) {

			log.info("Invalid consumer key");
			message.setError("Unauthorized");
			message.setMessage("Invalid Key");
			return new ResponseEntity<Message>(message, HttpStatus.UNAUTHORIZED);
		}

		ProductDTO result = (ProductDTO) productService.updatePricingDetails(productDTO, productId);

		if (null == result.getPricingDTO()) {
			log.info("Pricing details could not be found for the product id " + productId);
			message.setError("Pricing details not found");
			message.setMessage("Pricing details could not be found for the given product id");
			return new ResponseEntity<Message>(message, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<ProductDTO>(result, HttpStatus.OK);

	}

}
