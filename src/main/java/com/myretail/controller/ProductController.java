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

import com.myretail.constants.ProductConstants;
import com.myretail.domain.ProductDTO;
import com.myretail.service.ProductService;
import com.myretail.util.Errors;
import com.myretail.util.Message;

@RestController
@RequestMapping("/products/v1")
public class ProductController {

	@Autowired
	ProductService productService;

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
	public ResponseEntity<?> getProductPricingDetails(@PathVariable String productId, @RequestParam("key") String key) {

		log.info("Calling REST method getProductPricingDetails");

		ProductDTO productPricingInfo = null;
		Message message = new Message();

		if (!key.equals(ProductConstants.CONSUMER_KEY)) {
			log.info("Invalid consumer key");
			message.setMessage("Unauthorized");
			message.setErrors(new Errors("key", "Invalid Key"));
			return new ResponseEntity<Message>(message, HttpStatus.UNAUTHORIZED);

		}

		try {
			productPricingInfo = (ProductDTO) productService.getProductDetails(productId);
			if (null == productPricingInfo) {
				log.info("Product pricing details not found for the product id "+productId);
				message.setMessage("Pricing details not found");
				message.setErrors(new Errors("productId", "Pricing details not found for the given product id"));
				return new ResponseEntity<Message>(message, HttpStatus.NOT_FOUND);
			}
		} catch (IOException e) {
			log.error("IO Exception occured "+e.getMessage());
			Message nessage = new Message();
			nessage.setMessage("IO Exception occured");
			nessage.setErrors(new Errors("productId", e.getMessage()));
			return new ResponseEntity<Message>(nessage, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ProductDTO>(productPricingInfo, HttpStatus.OK);
	}

	/**
	 * This method updates the pricing information for a given productId. This
	 * request required HTTP request to have Content-Type as application/json
	 * 
	 * @param productId
	 * @param accept
	 * @param contentType
	 * @param productDTO
	 * @return
	 */
	@RequestMapping(value = "/{productId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updatePricingDetails(@PathVariable String productId,
			@RequestBody ProductDTO productDTO, @RequestParam("key") String key) {
		
		log.info("Calling REST method updatePricingDetails");
		
		Message message = new Message();
		if (!key.equals(ProductConstants.CONSUMER_KEY)) {
			
			log.info("Invalid consumer key");
			message.setMessage("Unauthorized");
			message.setErrors(new Errors("key", "Invalid Key"));
			return new ResponseEntity<Message>(message, HttpStatus.UNAUTHORIZED);
		}

		ProductDTO result = (ProductDTO) productService.updatePricingDetails(productDTO, productId);

		if (null == result) {
			log.info("Pricing information is not found for the product id "+productId);
			message.setMessage("Pricing details not found");
			message.setErrors(new Errors("productId", "Pricing details not found for the given product id"));
			return new ResponseEntity<Message>(message, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<ProductDTO>(result, HttpStatus.OK);

	}

}
