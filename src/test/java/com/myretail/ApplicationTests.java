package com.myretail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.base.Predicates;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@RunWith(SpringRunner.class)

public class ApplicationTests {

	@Test
	public void testConfigure() throws Exception {

		Application app = new Application();
		SpringApplicationBuilder builder1 = Mockito.mock(SpringApplicationBuilder.class);
		SpringApplicationBuilder builder2 = builder1.sources(Application.class);
		Mockito.when(app.configure(builder1)).thenReturn(builder2);

	}

	@Test
	public void testApi() throws Exception {

		Application app = Mockito.mock(Application.class);
		Mockito.when(app.api()).thenReturn(new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.any()).paths(Predicates.not(PathSelectors.regex("/error"))).build());

	}

}