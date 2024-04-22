package org.julleon.catalogue.controller;


import org.julleon.catalogue.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProductRestControllerIT {

    @Autowired
    MockMvc mockMvc;


    @Test
    @Transactional
    @Sql("/sql/test_products.sql")
    void methodFindProduct_conditionProductExistsById_ReturnsProduct() throws Exception {
//        given
        int pathVariableProductId = 1;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/catalogue-api/products/" + pathVariableProductId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(builder -> builder.claim("scope", "view_catalogue").build()));

        Product expectedProduct = new Product(1, "Test Product 1", "Test Description 1");

//        when
        mockMvc.perform(request)
//                then
                .andDo(MockMvcResultHandlers.print())

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(expectedProduct.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(expectedProduct.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(expectedProduct.getDescription()))

                .andDo(MockMvcRestDocumentation.document(
                        "/catalogue/products/find_product",
                        Preprocessors.preprocessResponse(
                                Preprocessors.prettyPrint(), new HeadersModifyingOperationPreprocessor().remove("Vary")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("id").type("int").description("Product ID"),
                                PayloadDocumentation.fieldWithPath("title").type("string").description("Product Title"),
                                PayloadDocumentation.fieldWithPath("description").type("string").description("Product Description")
                        )
                ));
    }
}