package org.julleon.catalogue.repository;


import org.julleon.catalogue.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Integer> {


    @Query("select p from Product p where p.title ilike concat('%', :titlePart, '%')")
    Iterable<Product> findProductsWithFilter(@Param("titlePart") String titlePart);

//    Iterable<Product> findAllByTitleLikeIgnoreCase(String titlePart);

    List<Product> findAllByTitleLikeIgnoreCase(String title);
    List<Product> findByTitle(String title);
}
