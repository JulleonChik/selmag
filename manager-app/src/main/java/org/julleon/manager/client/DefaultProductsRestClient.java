package org.julleon.manager.client;

import lombok.RequiredArgsConstructor;
import org.julleon.manager.client.exception.BadRequestException;
import org.julleon.manager.controller.payload.CreateProductPayload;
import org.julleon.manager.controller.payload.UpdateProductPayload;
import org.julleon.manager.entity.Product;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.*;

@RequiredArgsConstructor
public class DefaultProductsRestClient implements ProductsRestClient {

    public static final ParameterizedTypeReference<List<Product>> PRODUCTS_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };
    private final RestClient restClient;

    @Override
    public List<Product> findAllProducts(String filter) {
        return this.restClient
                .get()
                .uri("catalogue-api/products?filter={filter}", filter)
                .retrieve()
                .body(PRODUCTS_TYPE_REFERENCE);
    }


    @Override
    public Product createProduct(CreateProductPayload createProductPayload) {
        try {
            return this.restClient
                    .post()
                    .uri("catalogue-api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createProductPayload)
                    .retrieve()
                    .body(Product.class);
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail = e.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException(
                    (problemDetail != null
                            ? (List<String>) Objects.requireNonNull(problemDetail.getProperties()).get("errors")
                            : Collections.singletonList(e.getMessage())));
        }
    }

    @Override
    public Optional<Product> findProductById(int productId) {
        try {
            Product product = this.restClient
                    .get()
                    .uri("catalogue-api/products/{productId}", productId)
                    .retrieve()
                    .body(Product.class);
            return Optional.ofNullable(product);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }

    }

    @Override
    public void updateProduct(int productId, UpdateProductPayload updateProductPayload) {
        try {
            this.restClient
                    .patch()
                    .uri("catalogue-api/products/{productId}", productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updateProductPayload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail = e.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException(
                    (problemDetail != null
                            ? (List<String>) Objects.requireNonNull(problemDetail.getProperties()).get("errors")
                            : Collections.singletonList(e.getMessage())));
        }
    }

    @Override
    public void deleteProductById(int productId) {
        try {
            this.restClient
                    .delete()
                    .uri("catalogue-api/products/{productId}", productId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException(e);
        }
    }
}
