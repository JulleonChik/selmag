package org.julleon.repository;

import org.julleon.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Predicate;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = Collections.synchronizedList(new LinkedList<>());


    @Override
    public List<Product> findAll() {
        return Collections.unmodifiableList(this.products);
    }

    @Override
    public Product save(Product product) {
        Integer maxProductId = this.products.stream()
                .max(Comparator.comparingInt(Product::getId))
                .map(Product::getId)
                .orElse(0);
        product.setId(maxProductId + 1);
        this.products.add(product);
        return product;
    }

    @Override
    public Optional<Product> findById(Integer productId) {
        return this.products.stream()
                .filter(product -> Objects.equals(product.getId(), productId))
                .findFirst();
    }

    @Override
    public void deleteById(Integer productId) {
        this.products
                .removeIf(product -> Objects.equals(product.getId(), productId));
    }

}
