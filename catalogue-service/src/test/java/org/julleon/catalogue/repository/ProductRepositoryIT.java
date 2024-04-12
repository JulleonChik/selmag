package org.julleon.catalogue.repository;

import lombok.extern.slf4j.Slf4j;
import org.julleon.catalogue.entity.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringJUnitConfig
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:16:///selmag_catalogue?TC_DAEMON=true",
        "spring.datasource.username=catalogue",
        "spring.datasource.password=catalogue"
})
@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryIT {

    @Autowired
    private ProductRepository productRepository;


    @Test
    @Sql("/sql/products.sql")
    void findProductsWithFilter_thenGetIterableProducts() {
        String filterTitlePart = "фе";
//        when
        Iterable<Product> expectedProductsWithFilter = List.of(
                new Product(2, "Кофе", "Арабика"));
        Iterable<Product> actualProductsWithFilter = this.productRepository.findProductsWithFilter(filterTitlePart);
//        then
        Assertions.assertEquals(expectedProductsWithFilter, actualProductsWithFilter);
    }

}