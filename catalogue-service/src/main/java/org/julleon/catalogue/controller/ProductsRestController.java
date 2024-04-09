package org.julleon.catalogue.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.julleon.catalogue.controller.payload.CreateProductPayload;
import org.julleon.catalogue.entity.Product;
import org.julleon.catalogue.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j

@RestController
@RequestMapping("catalogue-api/products")
@RequiredArgsConstructor
public class ProductsRestController {
    private final ProductService productService;



    @GetMapping
    public Iterable<Product> findProducts(
            @RequestParam(name = "filter", required = false) String filter
    ) {
        return this.productService.findProductsByTitlePart(filter);
    }


    @PostMapping
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
