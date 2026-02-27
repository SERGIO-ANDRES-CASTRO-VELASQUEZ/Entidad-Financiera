package com.prueba.banco.repository;


import com.prueba.banco.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {

    boolean existsByNumberAccount(Integer numberAccount);

    Optional<Account> findByNumberAccount(Integer numberAccount);

}
