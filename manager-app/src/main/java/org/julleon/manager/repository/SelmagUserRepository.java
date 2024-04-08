package org.julleon.manager.repository;


import org.julleon.manager.entity.SelmagUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SelmagUserRepository extends CrudRepository<SelmagUser, Integer> {

    @Query(value = "select * from user_management.t_user where c_username = :c_username", nativeQuery = true)
    Optional<SelmagUser> findByUsername(@Param("c_username") String username);
}
