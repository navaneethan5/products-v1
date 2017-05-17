package com.myretail.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.myretail.Application;
import com.myretail.constants.ProductConstants;
import com.myretail.domain.PricingDTO;
import com.myretail.domain.ProductDTO;
import com.myretail.repository.ProductRepository;
import com.myretail.service.ProductService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ProductControllerUnitTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	MockHttpSession session;

	public ProductDTO productDTO;
	public PricingDTO pricingDTO;

	@Mock
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductController productController;

	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);

		assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		productController = new ProductController(productService);

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

		session.setAttribute("productDTO", productDTO);
	}

	@Test
	public void testGetProductPricingDetails() throws Exception {
		Mockito.when((ProductDTO) productService.getProductDetails("12345")).thenReturn(productDTO);
		ResponseEntity<?> response = productController.getProductPricingDetails("12345", ProductConstants.CONSUMER_KEY);
		assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	public void testUpdatePricingDetails() throws Exception {
		Mockito.when((ProductDTO) productService.updatePricingDetails(productDTO, "12345")).thenReturn(productDTO);
		ResponseEntity<?> response = productController.updatePricingDetails("12345", productDTO,
				ProductConstants.CONSUMER_KEY);
		assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	public void testGetPricingDetailsNotFound() throws Exception {
		ProductDTO productDTO = new ProductDTO("Sample Test Name", "12345", null);
		Mockito.when((ProductDTO) productService.getProductDetails("12345")).thenReturn(productDTO);
		ResponseEntity<?> response = productController.getProductPricingDetails("12345", ProductConstants.CONSUMER_KEY);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void testGetProductDetailsNotFound() throws Exception {
		ProductDTO productDTO = new ProductDTO(null, "12345", null);
		Mockito.when((ProductDTO) productService.getProductDetails("12345")).thenReturn(productDTO);
		ResponseEntity<?> response = productController.getProductPricingDetails("12345", ProductConstants.CONSUMER_KEY);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void testUpdatePricingDetailsNotFound() throws Exception {
		ProductDTO productDTO = new ProductDTO("Sample Test Name", "12345", null);
		Mockito.when((ProductDTO) productService.updatePricingDetails(productDTO, "12345")).thenReturn(productDTO);
		ResponseEntity<?> response = productController.updatePricingDetails("12345", productDTO,
				ProductConstants.CONSUMER_KEY);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void testGetProductPricingDetailsInvalidKey() throws Exception {
		mockMvc.perform(get("/products/v1/1386042?key=123").session(session).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

	}

	@Test
	public void testUpdatePricingDetailsInvalidKey() throws Exception {
		String jsonPath = json(
				new ProductDTO("The Big Lebowski (Blu-ray)", "13860428", new PricingDTO("138604", 100.0, "USD")));
		this.mockMvc
				.perform(put("/products/v1/138604?key=123").contentType(MediaType.APPLICATION_JSON).content(jsonPath))
				.andExpect(status().isUnauthorized());
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
