package org.julleon.catalogue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.julleon.catalogue.controller.payload.CreateProductPayload;
import org.julleon.catalogue.entity.Product;
import org.julleon.catalogue.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("catalogue-api/products")
@RequiredArgsConstructor
public class ProductsRestController {
    private final ProductService productService;



    @GetMapping
    @Operation(security = @SecurityRequirement(name = "keycloak"))
    public Iterable<Product> findProducts(
            @RequestParam(name = "filter", required = false) String filter
            ) {
        return this.productService.findProductsByTitlePart(filter);
    }


    @PostMapping
    @Operation(
            security = @SecurityRequirement(name = "keycloak"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    type = "object",
                                    properties = {
                                            @StringToClassMapItem(key = "title", value = String.class),
                                            @StringToClassMapItem(key = "description", value = String.class)
                                    }
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            headers = @Header(name = "Content-Type", description = "Data Type"),
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(
                                                    type = "object",
                                                    properties = {
                                                            @StringToClassMapItem(key = "id", value = Integer.class),
                                                            @StringToClassMapItem(key = "title", value = String.class),
                                                            @StringToClassMapItem(key = "description", value = String.class)
                                                    }
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<Product> createProduct(
            @RequestBody @Valid CreateProductPayload productPayload,
            UriComponentsBuilder uriComponentsBuilder,
            BindingResult bindingResult
    ) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            Product product = this.productService.createProduct(productPayload);
            URI uri = uriComponentsBuilder
                    .replacePath("/catalogue-api/products/{productId}")
                    .build(Map.of("productId", product.getId()));
            return ResponseEntity
                    .created(uri)
                    .body(product);
        }
    }
}
