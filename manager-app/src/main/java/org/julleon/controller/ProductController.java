package org.julleon.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.julleon.controller.payload.UpdateProductPayload;
import org.julleon.entity.Product;
import org.julleon.service.ProductService;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("catalogue/products/{productId:\\d+}")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final MessageSource messageSource;

    @ModelAttribute(value = "product", binding = false)
    public Product product(
            @PathVariable("productId") int productId
    ) {
        return this.productService.findProductById(productId)
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
            @Valid UpdateProductPayload payload,
            BindingResult bindingResult,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            model.addAttribute("payload", payload);
            model.addAttribute("errorMessages", errorMessages);
            return "catalogue/products/edit_product";
        } else {
            this.productService.updateProduct(product.getId(), payload);
            return "redirect:/catalogue/products/list";
        }
    }

    @PostMapping("delete")
    public String deleteProduct(
            @ModelAttribute("product") Product product
    ) {
        this.productService.deleteProduct(product.getId());
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
