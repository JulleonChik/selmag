package org.julleon.catalogue.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:16:///selmag_catalogue?TC_DAEMON=true",
        "spring.datasource.username=catalogue",
        "spring.datasource.password=catalogue"
})
class ProductsRestControllerIT {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @Sql("/sql/products.sql")
    void findProducts_ReturnsProductList() throws Exception {
//         given
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/catalogue-api/products")
                .param("filter", "товар")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));
//         when
        this.mockMvc.perform(requestBuilder)
//         then
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json("""
                                [
                                    {"id": 1, "title": "Товар №1", "description": "Описание товара №1"},
                                    {"id": 3, "title": "Товар №3", "description": "Описание товара №3"}
                                ]""")
                );
    }

    @Test
    void createProduct_withRequestValidPayload_thenReturnsNewProduct() throws Exception {
//        given
        SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
                SecurityMockMvcRequestPostProcessors.jwt().jwt(builder -> builder.claim("scope", "edit_catalogue"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                           {"title": "Новый товар", "description": "Описание нового товара"}
                        """)
                .with(jwtRequestPostProcessor);

//        then
        this.mockMvc.perform(requestBuilder)
//        when
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, "http://localhost/catalogue-api/products/1"),
                        MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json("""
                                {
                                    "id": 1,
                                    "title": "Новый товар",
                                    "description": "Описание нового товара"
                                }
                                """)
                );
    }

    @Test
    void createProduct_withRequestInvalidPayload_thenReturnsProblemDetail() throws Exception {
//        given
        SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
                SecurityMockMvcRequestPostProcessors.jwt().jwt(builder -> builder.claim("scope", "edit_catalogue"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .locale(new Locale.Builder().setLanguage("ru").setRegion("RU").build())
                .content("""
                        {
                            "title": "a",
                            "description": ""
                        }
                        """)
                .with(jwtRequestPostProcessor);
//        when
        mockMvc.perform(requestBuilder)
//        then
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        MockMvcResultMatchers.content().json("""
                                {
                                    "errors": [
                                        "Название товара должно быть от 3 до 50 символов",
                                        "Описание товара не должно быть пустым"
                                    ]
                                }
                                """)
                );
    }

    @Test
    void createProduct_withRequestFromUnauthorizedUser_thenReturnsForbidden() throws Exception {

        SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
                SecurityMockMvcRequestPostProcessors.jwt().jwt(builder -> builder.claim("scope", "view_catalogue"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .with(jwtRequestPostProcessor);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isForbidden()
                );
    }
}