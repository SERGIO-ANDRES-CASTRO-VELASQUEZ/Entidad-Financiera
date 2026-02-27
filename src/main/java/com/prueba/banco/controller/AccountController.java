package com.prueba.banco.controller;

import com.prueba.banco.DTO.accountDTOs.AccountRequestDTO;
import com.prueba.banco.DTO.accountDTOs.AccountResponseDTO;
import com.prueba.banco.models.ENUMS.TypeState;
import com.prueba.banco.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<AccountResponseDTO> updateAccountState(@PathVariable Long id, @RequestParam TypeState state) {
        return ResponseEntity.ok(accountService.updateAccountState(id, state));
    }
}
