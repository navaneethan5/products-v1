package com.myretail.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.constants.ProductConstants;
import com.myretail.controller.ProductController;
import com.myretail.domain.PricingDTO;
import com.myretail.domain.ProductDTO;
import com.myretail.repository.ProductRepository;

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

		PricingDTO pricingDTO = productPricingRepository.findByProductId(productId);

		if (null != pricingDTO.getProductId()) {

			pricingDTO.setCurrentPrice(productDTO.getPricingDTO().getCurrentPrice());
			pricingDTO.setCurrencyCode(productDTO.getPricingDTO().getCurrencyCode());

			productPricingRepository.save(pricingDTO);
		}

		productDTO.setPricingDTO(pricingDTO);

		return productDTO;
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

		if (productDetails instanceof ProductDTO && (null != ((ProductDTO) productDetails).getProductName())) {
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

		log.info("Calling Service method getProductName");

		ProductDTO productDetails = new ProductDTO();
		HttpURLConnection conn = null;

		try {

			URL url = new URL(constructURL(productId));
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			// RestTemplate restTemplate = new RestTemplate();
			// response = restTemplate.getForEntity(constructURL(productId),
			// String.class);

			if (conn.getResponseCode() == 200) {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode root = mapper.readTree(conn.getInputStream());
				JsonNode tcin = root.path("product").path("item").path("tcin");
				JsonNode title = root.path("product").path("item").path("product_description").path("title");
				productDetails.setProductId(tcin.asText());
				productDetails.setProductName(title.asText());
			}

			return productDetails;
		} catch (JsonProcessingException e) {
			log.error("JsonProcessingException occured when processing the response " + e.getMessage());
			return productDetails;
		}

		catch (IOException e) {
			log.error("IOException occured when invoking the api " + e.getMessage());
			return productDetails;
		}
		finally{
			conn.disconnect();
		}

	}

	/**
	 * Return target URL
	 * 
	 * @param productId
	 * @return
	 */
	public String constructURL(String productId) {
		return ProductConstants.REDSKY_ENDPOINT + "/" + productId;
	}
}
