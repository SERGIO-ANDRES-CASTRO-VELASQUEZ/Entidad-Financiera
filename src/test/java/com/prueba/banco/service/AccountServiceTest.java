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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AccountService accountService;

    private Customers customer;
    private Account account;
    private AccountRequestDTO requestDTO;
    private AccountResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        customer = new Customers();
        customer.setId(1L);
        customer.setFirstName("Sergio");
        customer.setLastName("Castro");

        account = new Account();
        account.setId(1L);
        account.setTypeAccount(TypeAccount.AHORROS);
        account.setNumberAccount(5312345678L);
        account.setTypeState(TypeState.ACTIVO);
        account.setBalance(new BigDecimal("500000"));
        account.setGMFexempt(false);
        account.setCustomer(customer);

        requestDTO = new AccountRequestDTO();
        requestDTO.setTypeAccount(TypeAccount.AHORROS);
        requestDTO.setCustomerId(1L);
        requestDTO.setBalance(new BigDecimal("500000"));

        responseDTO = new AccountResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTypeAccount(TypeAccount.AHORROS);
        responseDTO.setNumberAccount(5312345678L);
        responseDTO.setTypeState(TypeState.ACTIVO);
        responseDTO.setBalance(new BigDecimal("500000"));
        responseDTO.setCustomerId(1L);
        responseDTO.setCustomerFullName("Sergio Castro");
    }

    @Test
    void createAccount_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.existsByNumberAccount(anyLong())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(modelMapper.map(any(Account.class), eq(AccountResponseDTO.class))).thenReturn(responseDTO);

        AccountResponseDTO result = accountService.createAccount(requestDTO);

        assertNotNull(result);
        assertEquals(TypeState.ACTIVO, result.getTypeState());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_customerNotFound_throwsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> accountService.createAccount(requestDTO));
    }

    @Test
    void getAllAccounts_returnsList() {
        when(accountRepository.findAll()).thenReturn(List.of(account));
        when(modelMapper.map(any(Account.class), eq(AccountResponseDTO.class))).thenReturn(responseDTO);

        List<AccountResponseDTO> result = accountService.getAllAccounts();

        assertEquals(1, result.size());
    }

    @Test
    void getAccountById_found() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(modelMapper.map(any(Account.class), eq(AccountResponseDTO.class))).thenReturn(responseDTO);

        AccountResponseDTO result = accountService.getAccountById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAccountById_notFound_throwsException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AccoundNotFoundExcepcion.class, () -> accountService.getAccountById(99L));
    }

    @Test
    void updateAccountState_activar() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(modelMapper.map(any(Account.class), eq(AccountResponseDTO.class))).thenReturn(responseDTO);

        AccountResponseDTO result = accountService.updateAccountState(1L, TypeState.INACTIVO);

        assertNotNull(result);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void updateAccountState_cancelar_conSaldo_throwsException() {
        account.setBalance(new BigDecimal("100000"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(AccountStatusException.class,
                () -> accountService.updateAccountState(1L, TypeState.CANCELADO));
    }

    @Test
    void updateAccountState_cancelar_sinSaldo_success() {
        account.setBalance(BigDecimal.ZERO);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(modelMapper.map(any(Account.class), eq(AccountResponseDTO.class))).thenReturn(responseDTO);

        AccountResponseDTO result = accountService.updateAccountState(1L, TypeState.CANCELADO);

        assertNotNull(result);
    }
}
