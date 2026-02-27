package com.prueba.banco.repository;


import com.prueba.banco.models.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customers, Long> {

    Optional<Customers> findByNumberIdentity (String numberIdentity);

    Optional<Customers> findByEmail (String email);

    boolean existsByNumberIdentity (String numberIdentity);

    

}
