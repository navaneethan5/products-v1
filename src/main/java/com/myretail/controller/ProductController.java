package com.myretail.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myretail.constants.ProductConstants;
import com.myretail.domain.PricingDTO;
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
		if (null == productPricingInfo) {
			log.info("Error occured while retriving product name" + productId);
			message.setError("Product details could not be retrived");
			message.setMessage("Error occured while retriving product name");
			return new ResponseEntity<Message>(message, HttpStatus.NOT_FOUND);
		} else if (null == productPricingInfo.getProductName()) {
			log.info("Product name could not be found for the product id " + productId);
			productPricingInfo = new ProductDTO("", productId, new PricingDTO(productId, 0, ""));
			return new ResponseEntity<ProductDTO>(productPricingInfo, HttpStatus.NOT_FOUND);
		} else if (null == productPricingInfo.getPricingDTO()) {
			log.info("Pricing details could not be found for the product id " + productId);
			ProductDTO prodDTO = new ProductDTO(productPricingInfo.getProductName(), productPricingInfo.getProductId(),
					new PricingDTO(productPricingInfo.getProductId(), 0, ""));
			return new ResponseEntity<ProductDTO>(prodDTO, HttpStatus.NOT_FOUND);
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
	public ResponseEntity<?> updatePricingDetails(@PathVariable String productId,
			@Valid @RequestBody ProductDTO productDTO, @RequestParam("key") String key) {

		log.info("Calling REST method updatePricingDetails");

		Message message = new Message();

		if (!key.equals(ProductConstants.CONSUMER_KEY)) {

			log.info("Invalid consumer key");
			message.setError("Unauthorized");
			message.setMessage("Invalid Key");
			return new ResponseEntity<Message>(message, HttpStatus.UNAUTHORIZED);
		}

		else if (StringUtils.isEmpty(productDTO.getProductId())) {
			log.info("Product id is null");
			message.setError("Bad Request");
			message.setMessage("Product id cannot be null/empty");
			return new ResponseEntity<Message>(message, HttpStatus.NOT_FOUND);
		}

		else if (StringUtils.isEmpty(productDTO.getPricingDTO().getCurrencyCode())) {
			log.info("Currency code is null");
			message.setError("Bad Request");
			message.setMessage("Currency code cannot be empty");
			return new ResponseEntity<Message>(message, HttpStatus.NOT_FOUND);
		}

		else if (!StringUtils.isEmpty(productDTO.getPricingDTO().getCurrencyCode())
				&& productDTO.getPricingDTO().getCurrencyCode().length() != 3) {
			log.info("Currency code length is greater than 3 " + productDTO.getPricingDTO().getCurrencyCode());
			message.setError("Bad Request");
			message.setMessage("Currency code should be in 3 digits");
			return new ResponseEntity<Message>(message, HttpStatus.NOT_FOUND);
		}

		else if (productDTO.getPricingDTO().getCurrentPrice() <= 0) {
			log.info("Price cannot be equal/less than zero " + productDTO.getPricingDTO().getCurrentPrice());
			message.setError("Bad Request");
			message.setMessage("Price cannot be equal/less than zero");
			return new ResponseEntity<Message>(message, HttpStatus.NOT_FOUND);
		}

		ProductDTO result = (ProductDTO) productService.updatePricingDetails(productDTO, productId);

		if (null == result.getPricingDTO()) {
			log.info("Pricing details could not be found for the product id " + productId);
			ProductDTO prodDTO = new ProductDTO(result.getProductName(), result.getProductId(),
					new PricingDTO(result.getProductId(), 0, ""));
			return new ResponseEntity<ProductDTO>(prodDTO, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<ProductDTO>(result, HttpStatus.OK);

	}

}