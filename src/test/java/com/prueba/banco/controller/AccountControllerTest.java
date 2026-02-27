package com.prueba.banco.controller;

import com.prueba.banco.DTO.accountDTOs.AccountResponseDTO;
import com.prueba.banco.exception.AccoundNotFoundExcepcion;
import com.prueba.banco.exception.AccountStatusException;
import com.prueba.banco.exception.GlobalExceptionHandler;
import com.prueba.banco.models.ENUMS.TypeAccount;
import com.prueba.banco.models.ENUMS.TypeState;
import com.prueba.banco.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private AccountResponseDTO responseDTO;

    private static final String ACCOUNT_JSON = """
            {
                "typeAccount": "AHORROS",
                "customerId": 1,
                "balance": 500000
            }
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

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
    void createAccount_returns201() throws Exception {
        when(accountService.createAccount(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ACCOUNT_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.typeAccount").value("AHORROS"));
    }

    @Test
    void getAllAccounts_returns200() throws Exception {
        when(accountService.getAllAccounts()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numberAccount").value(5312345678L));
    }

    @Test
    void getAccountById_returns200() throws Exception {
        when(accountService.getAccountById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAccountById_notFound_returns404() throws Exception {
        when(accountService.getAccountById(99L)).thenThrow(new AccoundNotFoundExcepcion("99"));

        mockMvc.perform(get("/api/accounts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAccountState_returns200() throws Exception {
        when(accountService.updateAccountState(eq(1L), eq(TypeState.INACTIVO))).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/accounts/1/state")
                        .param("state", "INACTIVO"))
                .andExpect(status().isOk());
    }

    @Test
    void updateAccountState_cancelarConSaldo_returns400() throws Exception {
        when(accountService.updateAccountState(eq(1L), eq(TypeState.CANCELADO)))
                .thenThrow(new AccountStatusException("5312345678", "con saldo $0"));

        mockMvc.perform(patch("/api/accounts/1/state")
                        .param("state", "CANCELADO"))
                .andExpect(status().isBadRequest());
    }
}
