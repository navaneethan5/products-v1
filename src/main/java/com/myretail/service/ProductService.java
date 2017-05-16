package com.myretail.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.constants.ProductConstants;
import com.myretail.controller.ProductController;
import com.myretail.domain.PricingDTO;
import com.myretail.domain.ProductDTO;
import com.myretail.repository.ProductRepository;
import com.myretail.util.Errors;
import com.myretail.util.Message;

@Service
public class ProductService {

	ProductRepository productPricingRepository;

	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	public ProductService(ProductRepository productPricingRepository) {
		this.productPricingRepository = productPricingRepository;
	}

	/**
	 * Update pricing information for specific product id in database
	 * 
	 * @param productDTO
	 * @param productId
	 * @return
	 */
	public Object updatePricingDetails(ProductDTO productDTO, String productId) {

		log.info("Calling Service method updatePricingDetails");

		try {

			PricingDTO pricingDTO = productPricingRepository.findByProductId(productId);
			System.out.println("pricingDTO "+pricingDTO);

			if (null == pricingDTO) {
				return pricingDTO;
			}

			if (pricingDTO.getProductId() != null) {
				
				pricingDTO.setCurrentPrice(productDTO.getPricingDTO().getCurrentPrice());
				pricingDTO.setCurrencyCode(productDTO.getPricingDTO().getCurrencyCode());

				productPricingRepository.save(pricingDTO);
			}

			productDTO.setPricingDTO(pricingDTO);

			return productDTO;

		} catch (Exception e) {
			Message message = new Message();
			message.setErrors(new Errors("productId", e.getMessage()));
			message.setMessage(ProductConstants.DB_ERROR_CANNOT_RETRIEVE_PRICING);
			return message;
		}
	}

	/**
	 * Get product name from external API and retrieve pricing information from
	 * database for the give product id
	 * 
	 * @param productId
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public Object getProductDetails(String productId) throws JsonProcessingException, IOException {

		log.info("Calling Service method getProductDetails");
		Object productDetails = getProductName(productId);

		if (productDetails instanceof ProductDTO) {
			PricingDTO productPricing = productPricingRepository.findByProductId(productId);
			((ProductDTO) productDetails).setPricingDTO(productPricing);
		}

		return productDetails;

	}

	/**
	 * Call external API to retrieve product name for the given productId
	 * 
	 * @param productId
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public Object getProductName(String productId) throws JsonProcessingException, IOException {
		ResponseEntity<String> response = null;
		log.info("Calling Service method getProductName");
		try {

			ProductDTO productDetails = new ProductDTO();
			RestTemplate restTemplate = new RestTemplate();
			response = restTemplate.getForEntity(constructURL(productId), String.class);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.getBody());
			JsonNode tcin = root.path("product").path("item").path("tcin");
			JsonNode title = root.path("product").path("item").path("product_description").path("title");
			productDetails.setProductId(tcin.asText());
			productDetails.setProductName(title.asText());
			return productDetails;
		}

		catch (Exception e) {
			log.error("Exception occured: " + e.getMessage());
			return response;
		}

	}

	/**
	 * Return target URL
	 * 
	 * @param productId
	 * @return
	 */
	private String constructURL(String productId) {
		return ProductConstants.REDSKY_ENDPOINT + "/" + productId;
	}
}
