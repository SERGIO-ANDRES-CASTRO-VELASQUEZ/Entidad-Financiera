package com.prueba.banco.service;

import com.prueba.banco.DTO.TransactionDTOs.TransactionResponseDTO;
import com.prueba.banco.DTO.TransactionDTOs.TransactionsRequestDTO;
import com.prueba.banco.exception.AccoundNotFoundExcepcion;
import com.prueba.banco.exception.AccountStatusException;
import com.prueba.banco.exception.InsufficientBalanceException;
import com.prueba.banco.models.Account;
import com.prueba.banco.models.ENUMS.TypeState;
import com.prueba.banco.models.ENUMS.TypeTransaction;
import com.prueba.banco.models.Transaction;
import com.prueba.banco.repository.AccountRepository;
import com.prueba.banco.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionResponseDTO processTransaction(TransactionsRequestDTO request) {
        return switch (request.getTypeTransaction()) {
            case CONSIGNACION -> processDeposit(request);
            case RETIRO -> processWithdrawal(request);
            case TRANSFERENCIA -> processTransfer(request);
        };
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByAccount(Long accountNumber) {
        Account account = accountRepository.findByNumberAccount(accountNumber)
                .orElseThrow(() -> new AccoundNotFoundExcepcion(accountNumber.toString()));

        return transactionRepository
                .findBySourceAccountOrTargetAccountOrderByCreatedDateDesc(account, account)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponseDTO processDeposit(TransactionsRequestDTO request) {
        Account targetAccount = accountRepository.findByNumberAccount(request.getTargetAccountNumber())
                .orElseThrow(() -> new AccoundNotFoundExcepcion(request.getTargetAccountNumber().toString()));

        validateActiveAccount(targetAccount);

        targetAccount.setBalance(targetAccount.getBalance().add(request.getAmount()));
        accountRepository.save(targetAccount);

        Transaction transaction = new Transaction();
        transaction.setTypeTransaction(TypeTransaction.CONSIGNACION);
        transaction.setAmount(request.getAmount());
        transaction.setTargetAccount(targetAccount);

        transaction = transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    private TransactionResponseDTO processWithdrawal(TransactionsRequestDTO request) {
        Account sourceAccount = accountRepository.findByNumberAccount(request.getSourceAccountNumber())
                .orElseThrow(() -> new AccoundNotFoundExcepcion(request.getSourceAccountNumber().toString()));

        validateActiveAccount(sourceAccount);
        validateSufficientBalance(sourceAccount, request.getAmount());

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(sourceAccount);

        Transaction transaction = new Transaction();
        transaction.setTypeTransaction(TypeTransaction.RETIRO);
        transaction.setAmount(request.getAmount());
        transaction.setSourceAccount(sourceAccount);

        transaction = transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    private TransactionResponseDTO processTransfer(TransactionsRequestDTO request) {
        Account sourceAccount = accountRepository.findByNumberAccount(request.getSourceAccountNumber())
                .orElseThrow(() -> new AccoundNotFoundExcepcion(request.getSourceAccountNumber().toString()));
        Account targetAccount = accountRepository.findByNumberAccount(request.getTargetAccountNumber())
                .orElseThrow(() -> new AccoundNotFoundExcepcion(request.getTargetAccountNumber().toString()));

        validateActiveAccount(sourceAccount);
        validateActiveAccount(targetAccount);
        validateSufficientBalance(sourceAccount, request.getAmount());

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        targetAccount.setBalance(targetAccount.getBalance().add(request.getAmount()));

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        Transaction transaction = new Transaction();
        transaction.setTypeTransaction(TypeTransaction.TRANSFERENCIA);
        transaction.setAmount(request.getAmount());
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);

        transaction = transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    private void validateActiveAccount(Account account) {
        if (account.getTypeState() != TypeState.ACTIVO) {
            throw new AccountStatusException(account.getNumberAccount().toString(), "ACTIVO");
        }
    }

    private void validateSufficientBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    account.getNumberAccount().toString(),
                    account.getBalance().doubleValue(),
                    amount.doubleValue()
            );
        }
    }

    private TransactionResponseDTO mapToResponse(Transaction transaction) {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(transaction.getId());
        response.setTypeTransaction(transaction.getTypeTransaction());
        response.setAmount(transaction.getAmount());
        response.setCreatedDate(transaction.getCreatedDate());
        response.setSourceAccountNumber(
                transaction.getSourceAccount() != null ? transaction.getSourceAccount().getNumberAccount() : null
        );
        response.setTargetAccountNumber(
                transaction.getTargetAccount() != null ? transaction.getTargetAccount().getNumberAccount() : null
        );
        return response;
    }
}
