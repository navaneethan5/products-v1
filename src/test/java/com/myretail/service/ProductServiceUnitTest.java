package com.myretail.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.myretail.constants.ProductConstants;
import com.myretail.domain.PricingDTO;
import com.myretail.domain.ProductDTO;
import com.myretail.repository.ProductRepository;
import com.myretail.service.ProductService;

@RunWith(SpringRunner.class)
public class ProductServiceUnitTest {

	private PricingDTO pricingDTO;
	private ProductDTO productDTO;

	private ProductService productService;
	private ProductService service;
	private ProductRepository productPricingRepository;
	
	private URL url = null;
	private HttpURLConnection conn;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
    
	@Before
	public void setup() throws IOException {

		productPricingRepository = Mockito.mock(ProductRepository.class);
		productService = new ProductService(productPricingRepository);
		service = Mockito.mock(ProductService.class);
		
		conn = Mockito.mock(HttpURLConnection.class);
		

		// set productPricing
		pricingDTO = new PricingDTO();
		pricingDTO.setCurrencyCode("USD");
		pricingDTO.setCurrentPrice(19.99);
		pricingDTO.setProductId("12345");

		// set productDTO
		productDTO = new ProductDTO();
		productDTO.setProductId("12345");
		productDTO.setProductName("The Big Lebowski (Blu-ray)");
		productDTO.setPricingDTO(pricingDTO);
	}

	@Test
	public void testGetProductDetails() throws Exception {
		
		Mockito.when(productPricingRepository.findByProductId("12345")).thenReturn(pricingDTO);
		
		Object response = productService.getProductDetails("12345");
		assertNotNull(response);
	}

	
	@Test
	public void testUpdatePricingDetails() throws Exception {
		Mockito.when(productPricingRepository.findByProductId("12345")).thenReturn(pricingDTO);
		Object response = productService.updatePricingDetails(productDTO, "12345");
		assertNotNull(response);
	}
	

	
//	@Test
//	public void testGetPricingDetailsE() throws Exception {		
//		Mockito.when(service.getProductName("12345")).thenThrow(IOException.class);
//		Object response = service.getProductName("12345");
//		assertNull(response);
//	}
	
//	@Test
//	public void testGetProductDetailsIOException() throws Exception {
//		Mockito.when((HttpURLConnection)url.openConnection()).thenThrow(IOException.class);
//		Object response = productService.getProductName( "12345");
//		thrown.expect(IOException.class);
//	}

}
