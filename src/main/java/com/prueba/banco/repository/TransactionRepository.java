package com.prueba.banco.repository;


import com.prueba.banco.models.Account;
import com.prueba.banco.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findBySourceAccountOrTargetAccountOrderByCreatedDateDesc(Account sourceAccount, Account targetAccount);



}
