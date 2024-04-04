package org.julleon.service;

import lombok.RequiredArgsConstructor;
import org.julleon.entity.Product;
import org.julleon.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public List<Product> findAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public Product createProduct(String title, String description) {
        return this.productRepository.save(new Product(null, title, description));
    }
}
