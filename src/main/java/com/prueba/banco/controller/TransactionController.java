package com.prueba.banco.controller;

import com.prueba.banco.DTO.TransactionDTOs.TransactionResponseDTO;
import com.prueba.banco.DTO.TransactionDTOs.TransactionsRequestDTO;
import com.prueba.banco.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionsRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.processTransaction(request));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByAccount(@PathVariable Long accountNumber) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountNumber));
    }
}
