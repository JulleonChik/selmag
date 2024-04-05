package org.julleon.catalogue.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.julleon.catalogue.controller.payload.UpdateProductPayload;
import org.julleon.catalogue.entity.Product;
import org.julleon.catalogue.service.ProductService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequestMapping("catalogue-api/products/{productId:\\d+}")
@RequiredArgsConstructor
public class ProductRestController {
    private final ProductService productService;
    private final MessageSource messageSource;

    @ModelAttribute(value = "product", binding = false)
    public Product getProduct(@PathVariable("productId") int productId) {
        return this.productService.findProductById(productId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
    }

    @GetMapping
    public Product findProduct(
            @ModelAttribute("product") Product product
    ) {
        return product;
    }

    @PatchMapping
    public ResponseEntity<Void> updateProduct(
            @PathVariable("productId") int productId,
            @RequestBody @Valid UpdateProductPayload payload,
            BindingResult bindingResult
    ) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            this.productService.updateProduct(productId, payload);
            return ResponseEntity
                    .noContent()
                    .build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("productId") int productId
    ) {
        this.productService.deleteProduct(productId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(
            NoSuchElementException elementException,
            Locale locale
    ) {
        ProblemDetail problemNotFoundDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                Objects.requireNonNull(this.messageSource.getMessage(elementException.getMessage(), new Object[0], elementException.getMessage(), locale))
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problemNotFoundDetail);

    }
}
