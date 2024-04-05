package org.julleon.manager.controller;


import lombok.RequiredArgsConstructor;
import org.julleon.manager.client.ProductsRestClient;
import org.julleon.manager.client.exception.BadRequestException;
import org.julleon.manager.controller.payload.CreateProductPayload;
import org.julleon.manager.entity.Product;
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
    private final ProductsRestClient productsRestClient;


    @GetMapping("create")
    public String getCreateProductForm() {
        return "catalogue/products/new_product";
    }


    @GetMapping("list")
    public String getProductsList(Model model) {
        List<Product> allProducts = this.productsRestClient.findAllProducts();
        model.addAttribute("products", allProducts);
        return "catalogue/products/list";
    }

    @PostMapping("create")
    public String createProduct(
            CreateProductPayload payload,
            Model model
    ) {
        try {
            Product product = this.productsRestClient.createProduct(payload);
            return "redirect:/catalogue/products/%d".formatted(product.id());
        } catch (BadRequestException e) {
            model.addAttribute("payload", payload);
            model.addAttribute("errorMessages", e.getErrorMessages());
            return "catalogue/products/new_product";
        }
    }
}