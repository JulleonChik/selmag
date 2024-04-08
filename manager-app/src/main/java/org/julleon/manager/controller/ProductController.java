package org.julleon.manager.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.julleon.manager.client.ProductsRestClient;
import org.julleon.manager.client.exception.BadRequestException;
import org.julleon.manager.controller.payload.UpdateProductPayload;

import org.julleon.manager.dto.Product;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("catalogue/products/{productId:\\d+}")
@RequiredArgsConstructor
public class ProductController {
    private final ProductsRestClient productsRestClient;
    private final MessageSource messageSource;

    @ModelAttribute(value = "product", binding = false)
    public Product product(
            @PathVariable("productId") int productId
    ) {
        return this.productsRestClient.findProductById(productId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
    }

    @GetMapping
    public String getProductById(
    ) {
        return "catalogue/products/product";
    }

    @GetMapping("edit")
    public String getProductEditPage(
    ) {
        return "catalogue/products/edit_product";
    }

    @PostMapping("edit")
    public String updateProduct(
            @ModelAttribute(value = "product") Product product,
            UpdateProductPayload payload,
            Model model
    ) {
        try {
            this.productsRestClient.updateProduct(product.id(), payload);
            return "redirect:/catalogue/products/list";
        } catch (BadRequestException e) {
            model.addAttribute("payload", payload);
            model.addAttribute("errorMessages", e.getErrorMessages());
            return "catalogue/products/edit_product";
        }
    }

    @PostMapping("delete")
    public String deleteProduct(
            @ModelAttribute("product") Product product
    ) {
        this.productsRestClient.deleteProductById(product.id());
        return "redirect:/catalogue/products/list";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(
            NoSuchElementException elementException,
            HttpServletResponse servletResponse,
            Model model,
            Locale locale
    ) {
        servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
        String errorMessage = messageSource.getMessage(elementException.getMessage(), new Object[0], elementException.getMessage(), locale);
        model.addAttribute("errorMessage", errorMessage);
        return "errors/404";
    }

}
