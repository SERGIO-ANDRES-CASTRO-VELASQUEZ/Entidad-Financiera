package com.prueba.banco.service;

import com.prueba.banco.DTO.TransactionDTOs.TransactionResponseDTO;
import com.prueba.banco.DTO.TransactionDTOs.TransactionsRequestDTO;
import com.prueba.banco.exception.AccoundNotFoundExcepcion;
import com.prueba.banco.exception.AccountStatusException;
import com.prueba.banco.exception.InsufficientBalanceException;
import com.prueba.banco.models.Account;
import com.prueba.banco.models.Customers;
import com.prueba.banco.models.ENUMS.TypeAccount;
import com.prueba.banco.models.ENUMS.TypeState;
import com.prueba.banco.models.ENUMS.TypeTransaction;
import com.prueba.banco.models.Transaction;
import com.prueba.banco.repository.AccountRepository;
import com.prueba.banco.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    void setUp() {
        Customers customer = new Customers();
        customer.setId(1L);
        customer.setFirstName("Sergio");
        customer.setLastName("Castro");

        sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setTypeAccount(TypeAccount.AHORROS);
        sourceAccount.setNumberAccount(5312345678L);
        sourceAccount.setTypeState(TypeState.ACTIVO);
        sourceAccount.setBalance(new BigDecimal("500000"));
        sourceAccount.setCustomer(customer);

        targetAccount = new Account();
        targetAccount.setId(2L);
        targetAccount.setTypeAccount(TypeAccount.CORRIENTE);
        targetAccount.setNumberAccount(3398765432L);
        targetAccount.setTypeState(TypeState.ACTIVO);
        targetAccount.setBalance(new BigDecimal("200000"));
        targetAccount.setCustomer(customer);
    }

    @Test
    void processDeposit_success() {
        TransactionsRequestDTO request = new TransactionsRequestDTO();
        request.setTypeTransaction(TypeTransaction.CONSIGNACION);
        request.setTargetAccountNumber(3398765432L);
        request.setAmount(new BigDecimal("100000"));

        Transaction saved = new Transaction();
        saved.setId(1L);
        saved.setTypeTransaction(TypeTransaction.CONSIGNACION);
        saved.setAmount(new BigDecimal("100000"));
        saved.setTargetAccount(targetAccount);

        when(accountRepository.findByNumberAccount(3398765432L)).thenReturn(Optional.of(targetAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(targetAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponseDTO result = transactionService.processTransaction(request);

        assertNotNull(result);
        assertEquals(TypeTransaction.CONSIGNACION, result.getTypeTransaction());
        assertEquals(new BigDecimal("300000"), targetAccount.getBalance());
    }

    @Test
    void processWithdrawal_success() {
        TransactionsRequestDTO request = new TransactionsRequestDTO();
        request.setTypeTransaction(TypeTransaction.RETIRO);
        request.setSourceAccountNumber(5312345678L);
        request.setAmount(new BigDecimal("100000"));

        Transaction saved = new Transaction();
        saved.setId(2L);
        saved.setTypeTransaction(TypeTransaction.RETIRO);
        saved.setAmount(new BigDecimal("100000"));
        saved.setSourceAccount(sourceAccount);

        when(accountRepository.findByNumberAccount(5312345678L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(sourceAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponseDTO result = transactionService.processTransaction(request);

        assertNotNull(result);
        assertEquals(new BigDecimal("400000"), sourceAccount.getBalance());
    }

    @Test
    void processWithdrawal_insufficientBalance_throwsException() {
        TransactionsRequestDTO request = new TransactionsRequestDTO();
        request.setTypeTransaction(TypeTransaction.RETIRO);
        request.setSourceAccountNumber(5312345678L);
        request.setAmount(new BigDecimal("999999"));

        when(accountRepository.findByNumberAccount(5312345678L)).thenReturn(Optional.of(sourceAccount));

        assertThrows(InsufficientBalanceException.class,
                () -> transactionService.processTransaction(request));
    }

    @Test
    void processTransfer_success() {
        TransactionsRequestDTO request = new TransactionsRequestDTO();
        request.setTypeTransaction(TypeTransaction.TRANSFERENCIA);
        request.setSourceAccountNumber(5312345678L);
        request.setTargetAccountNumber(3398765432L);
        request.setAmount(new BigDecimal("150000"));

        Transaction saved = new Transaction();
        saved.setId(3L);
        saved.setTypeTransaction(TypeTransaction.TRANSFERENCIA);
        saved.setAmount(new BigDecimal("150000"));
        saved.setSourceAccount(sourceAccount);
        saved.setTargetAccount(targetAccount);

        when(accountRepository.findByNumberAccount(5312345678L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByNumberAccount(3398765432L)).thenReturn(Optional.of(targetAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponseDTO result = transactionService.processTransaction(request);

        assertNotNull(result);
        assertEquals(new BigDecimal("350000"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("350000"), targetAccount.getBalance());
    }

    @Test
    void processTransaction_inactiveAccount_throwsException() {
        sourceAccount.setTypeState(TypeState.INACTIVO);

        TransactionsRequestDTO request = new TransactionsRequestDTO();
        request.setTypeTransaction(TypeTransaction.RETIRO);
        request.setSourceAccountNumber(5312345678L);
        request.setAmount(new BigDecimal("1000"));

        when(accountRepository.findByNumberAccount(5312345678L)).thenReturn(Optional.of(sourceAccount));

        assertThrows(AccountStatusException.class,
                () -> transactionService.processTransaction(request));
    }

    @Test
    void processTransaction_accountNotFound_throwsException() {
        TransactionsRequestDTO request = new TransactionsRequestDTO();
        request.setTypeTransaction(TypeTransaction.CONSIGNACION);
        request.setTargetAccountNumber(9999999999L);
        request.setAmount(new BigDecimal("1000"));

        when(accountRepository.findByNumberAccount(9999999999L)).thenReturn(Optional.empty());

        assertThrows(AccoundNotFoundExcepcion.class,
                () -> transactionService.processTransaction(request));
    }

    @Test
    void getTransactionsByAccount_success() {
        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setTypeTransaction(TypeTransaction.CONSIGNACION);
        tx.setAmount(new BigDecimal("50000"));
        tx.setTargetAccount(targetAccount);

        when(accountRepository.findByNumberAccount(3398765432L)).thenReturn(Optional.of(targetAccount));
        when(transactionRepository.findBySourceAccountOrTargetAccountOrderByCreatedDateDesc(targetAccount, targetAccount))
                .thenReturn(List.of(tx));

        List<TransactionResponseDTO> result = transactionService.getTransactionsByAccount(3398765432L);

        assertEquals(1, result.size());
    }
}
