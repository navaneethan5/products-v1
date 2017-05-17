package com.myretail.service;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.myretail.domain.PricingDTO;
import com.myretail.domain.ProductDTO;
import com.myretail.repository.ProductRepository;

@RunWith(SpringRunner.class)
public class ProductServiceUnitTest {

	private PricingDTO pricingDTO;
	private ProductDTO productDTO;

	private ProductService productService;
	private ProductRepository productPricingRepository;

	private ProductRepository repository;
	private ProductService service;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() throws IOException {

		productPricingRepository = Mockito.mock(ProductRepository.class);
		productService = new ProductService(productPricingRepository);
		service = new ProductService(repository);

		// set productPricing
		pricingDTO = new PricingDTO();
		pricingDTO.setCurrencyCode("USD");
		pricingDTO.setCurrentPrice(19.99);
		pricingDTO.setProductId("16696652");

		// set productDTO
		productDTO = new ProductDTO();
		productDTO.setProductId("16696652");
		productDTO.setProductName("Beats Solo 2 Wireless - Black (MHNG2AM/A)");
		productDTO.setPricingDTO(pricingDTO);
	}

	@Test
	public void testGetProductDetails() throws Exception {

		Mockito.when(productPricingRepository.findByProductId("16696652")).thenReturn(pricingDTO);
		Object response = productService.getProductDetails("16696652");
		assertNotNull(response);
	}

	@Test
	public void testUpdatePricingDetails() throws Exception {
		Mockito.when(productPricingRepository.findByProductId("16696652")).thenReturn(pricingDTO);
		Object response = productService.updatePricingDetails(productDTO, "16696652");
		assertNotNull(response);
	}

}