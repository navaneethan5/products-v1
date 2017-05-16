package com.tgt.myretail.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import com.myretail.domain.PricingDTO;
import com.myretail.domain.ProductDTO;
import com.myretail.repository.ProductRepository;
import com.myretail.service.ProductService;

@RunWith(SpringRunner.class)
public class ProductServiceUnitTest {

	private PricingDTO pricingDTO;
	private ProductDTO productDTO;

	private ProductService productService;
	private ProductRepository productPricingRepository;

	@Before
	public void setup() {

		productPricingRepository = Mockito.mock(ProductRepository.class);
		productService = new ProductService(productPricingRepository);

		// set productPricing
		pricingDTO = new PricingDTO();
		pricingDTO.setCurrencyCode("USD");
		pricingDTO.setCurrentPrice(19.99);
		pricingDTO.setProductId("13860428");

		// set productDTO
		productDTO = new ProductDTO();
		productDTO.setProductId("13860428");
		productDTO.setProductName("The Big Lebowski (Blu-ray)");
		productDTO.setPricingDTO(pricingDTO);
	}

	@Test
	public void testGetProductDetails() throws Exception {
		Mockito.when(productService.getProductDetails("13860428")).thenReturn(pricingDTO);
	}

	@Test
	public void testUpdatePricingDetails() throws Exception {
		Mockito.when(productService.updatePricingDetails(productDTO, "13860428")).thenReturn(pricingDTO);
	}

}
