package com.prueba.banco.service;

import com.prueba.banco.DTO.accountDTOs.AccountRequestDTO;
import com.prueba.banco.DTO.accountDTOs.AccountResponseDTO;
import com.prueba.banco.exception.AccoundNotFoundExcepcion;
import com.prueba.banco.exception.AccountStatusException;
import com.prueba.banco.exception.CustomerNotFoundException;
import com.prueba.banco.models.Account;
import com.prueba.banco.models.Customers;
import com.prueba.banco.models.ENUMS.TypeAccount;
import com.prueba.banco.models.ENUMS.TypeState;
import com.prueba.banco.repository.AccountRepository;
import com.prueba.banco.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO request) {
        Customers customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId().toString()));

        Account account = new Account();
        account.setTypeAccount(request.getTypeAccount());
        account.setNumberAccount(generateAccountNumber(request.getTypeAccount()));
        account.setTypeState(TypeState.ACTIVO);
        account.setBalance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO);
        account.setGMFexempt(request.getGMFexempt() != null ? request.getGMFexempt() : false);
        account.setCustomer(customer);

        account = accountRepository.save(account);
        return mapToResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccoundNotFoundExcepcion(id.toString()));
        return mapToResponse(account);
    }

    @Transactional
    public AccountResponseDTO updateAccountState(Long id, TypeState newState) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccoundNotFoundExcepcion(id.toString()));

        if (newState == TypeState.CANCELADO && account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountStatusException(
                    account.getNumberAccount().toString(),
                    "con saldo $0 para poder cancelar. Saldo actual: $" + account.getBalance()
            );
        }

        account.setTypeState(newState);
        account = accountRepository.save(account);
        return mapToResponse(account);
    }

    private Long generateAccountNumber(TypeAccount typeAccount) {
        String prefix = typeAccount == TypeAccount.AHORROS ? "53" : "33";
        Random random = new Random();
        Long accountNumber;

        do {
            StringBuilder sb = new StringBuilder(prefix);
            for (int i = 0; i < 8; i++) {
                sb.append(random.nextInt(10));
            }
            accountNumber = Long.parseLong(sb.toString());
        } while (accountRepository.existsByNumberAccount(accountNumber));

        return accountNumber;
    }

    private AccountResponseDTO mapToResponse(Account account) {
        AccountResponseDTO response = modelMapper.map(account, AccountResponseDTO.class);
        response.setCustomerId(account.getCustomer().getId());
        response.setCustomerFullName(account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName());
        return response;
    }
}
