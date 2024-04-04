package org.julleon.controller;


import lombok.RequiredArgsConstructor;
import org.julleon.controller.payload.NewProductPayload;
import org.julleon.entity.Product;
import org.julleon.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products")
public class ProductsController {
    private final ProductService productService;

    @GetMapping( "list")
    public String getProductsList(
            Model model
    ) {
        List<Product> allProducts = this.productService.findAllProducts();
        model.addAttribute("products", allProducts);
        return "catalogue/products/list";
    }

    @GetMapping("create")
    public String getCreateProductForm() {
        return "catalogue/products/form_create_product";
    }

    @PostMapping("create")
    public String createProduct(
            NewProductPayload payload
    ) {
        Product product = this.productService.createProduct(payload.title(), payload.description());
        return "redirect:/catalogue/products/list";
    }
}
