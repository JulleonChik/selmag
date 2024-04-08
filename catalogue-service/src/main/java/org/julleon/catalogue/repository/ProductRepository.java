package org.julleon.catalogue.repository;


import org.julleon.catalogue.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends CrudRepository<Product, Integer> {


    @Query("select p from Product p where p.title ilike concat('%', :titlePart, '%')")
    Iterable<Product> findProductsWithFilter(@Param("titlePart") String titlePart);

//    select * from catalogue.t_product p where c_title ilike: title
    Iterable<Product> findAllByTitleLikeIgnoreCase(String titlePart);


}
