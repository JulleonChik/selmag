package org.julleon.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.julleon.controller.payload.CreateProductPayload;
import org.julleon.entity.Product;
import org.julleon.service.ProductService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products")
public class ProductsController {
    private final ProductService productService;


    @GetMapping("create")
    public String getCreateProductForm() {
        return "catalogue/products/new_product";
    }


    @GetMapping("list")
    public String getProductsList(Model model) {
        List<Product> allProducts = this.productService.findAllProducts();
        model.addAttribute("products", allProducts);
        return "catalogue/products/list";
    }

    @PostMapping("create")
    public String createProduct(
            @Valid CreateProductPayload payload, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            model.addAttribute("payload", payload);
            model.addAttribute("errorMessages", errorMessages);
            return "catalogue/products/new_product";
        } else {
            Product product = this.productService.createProduct(payload);
            return "redirect:/catalogue/products/%d".formatted(product.getId());
        }
    }
}
