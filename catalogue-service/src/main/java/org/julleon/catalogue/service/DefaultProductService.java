package org.julleon.catalogue.service;

import lombok.RequiredArgsConstructor;

import org.julleon.catalogue.controller.payload.CreateProductPayload;
import org.julleon.catalogue.controller.payload.UpdateProductPayload;
import org.julleon.catalogue.entity.Product;
import org.julleon.catalogue.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;


    @Transactional(readOnly = true)
    @Override
    public Iterable<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Iterable<Product> findProductsByTitlePart(String partTitle) {
        if (StringUtils.hasLength(partTitle)) {
            return productRepository.findProductsWithFilter(partTitle);
        } else {
            return productRepository.findAll();
        }
    }

    @Transactional
    @Override
    public Product createProduct(CreateProductPayload createProductPayload) {
        Product product = new Product();
        product.setTitle(createProductPayload.title());
        product.setDescription(createProductPayload.description());
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Product> findProductById(int productId) {
        return productRepository.findById(productId);
    }

    @Transactional
    @Override
    public void updateProduct(int productId, UpdateProductPayload payload) {
        productRepository.findById(productId)
                .ifPresentOrElse(product -> {
                    product.setTitle(payload.title());
                    product.setDescription(payload.description());
//                    productRepository.save(product);
                }, () -> {
                    throw new NoSuchElementException();
                });
    }


    @Transactional
    @Override
    public void deleteProduct(int productId) {
        productRepository.findById(productId)
                .ifPresentOrElse(productRepository::delete, () -> {
                    throw new NoSuchElementException();
                });
    }
}
