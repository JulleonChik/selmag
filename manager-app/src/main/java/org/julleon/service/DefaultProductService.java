package org.julleon.service;

import lombok.RequiredArgsConstructor;
import org.julleon.controller.payload.CreateProductPayload;
import org.julleon.controller.payload.UpdateProductPayload;
import org.julleon.entity.Product;
import org.julleon.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public List<Product> findAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public Product createProduct(CreateProductPayload payload) {
        return this.productRepository.save(new Product(null, payload.title(), payload.description()));
    }

    @Override
    public Optional<Product> findProductById(int productId) {
        return this.productRepository.findById(productId);
    }


    @Override
    public void updateProduct(int productId, UpdateProductPayload payload) {
        this.productRepository.findById(productId)
                .ifPresentOrElse(product -> {
                    product.setTitle(payload.title());
                    product.setDescription(payload.description());
                }, () -> {
                    throw new NoSuchElementException();
                });
    }

    @Override
    public void deleteProduct(int productId) {
        this.productRepository.deleteById(productId);
    }


}
