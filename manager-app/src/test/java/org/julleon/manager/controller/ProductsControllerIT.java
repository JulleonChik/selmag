package org.julleon.manager.controller;


import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.julleon.manager.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 12341)
public class ProductsControllerIT {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getPageWithProductsList_ReturnsPageWithProductList() throws Exception {
//        setting mock ProductsRestClient
        UrlPathPattern restClientUri = WireMock.urlPathMatching("/catalogue-api/products");
        String filterRequestParam = "товар";
        ResponseDefinitionBuilder responseDefinitionBuilder =
                WireMock
                        .ok("""
                                [
                                    {"id": 1, "title": "Товар №1", "description": "Описание товара №1"},
                                    {"id": 2, "title": "Товар №2", "description": "Описание товара №2"},
                                    {"id": 3, "title": "Товар №3", "description": "Описание товара №3"}
                                ]
                                """)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        MappingBuilder mappingBuilder = WireMock
                .get(restClientUri)
                .withQueryParam("filter", WireMock.equalTo(filterRequestParam))
                .willReturn(responseDefinitionBuilder);
        StubMapping stubMapping = WireMock.stubFor(mappingBuilder);


//        given
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders
                        .get("/catalogue/products/list")
                        .queryParam("filter", filterRequestParam)
                        .with(SecurityMockMvcRequestPostProcessors.user("julleon").roles("MANAGER"));


        List<Product> expectedProductsFromProductsRestClient = List.of(
                new Product(1, "Товар №1", "Описание товара №1"),
                new Product(2, "Товар №2", "Описание товара №2"),
                new Product(3, "Товар №3", "Описание товара №3")
        );


//        when
        mockMvc.perform(requestBuilder)
//                then
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.view().name("catalogue/products/list"),
                        MockMvcResultMatchers.model().attribute("products", expectedProductsFromProductsRestClient),
                        MockMvcResultMatchers.model().attribute("filter", filterRequestParam)
                );

        WireMock.verify(
                WireMock.getRequestedFor(restClientUri)
                        .withQueryParam("filter", WireMock.equalTo(filterRequestParam))
        );
    }


    @Test
    void getPageWithFormForCreatingNewProduct_ReturnsPageWithFormForCreatingNewProduct() throws Exception {
//        given
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders
                        .get("/catalogue/products/create")
                        .with(SecurityMockMvcRequestPostProcessors.user("julleon").roles("MANAGER"));
//        when
        mockMvc
                .perform(requestBuilder)
//        then
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.view().name("catalogue/products/new_product")
                );
    }
}
