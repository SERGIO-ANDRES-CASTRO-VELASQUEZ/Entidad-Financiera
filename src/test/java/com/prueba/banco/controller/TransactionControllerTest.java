package com.prueba.banco.controller;

import com.prueba.banco.DTO.TransactionDTOs.TransactionResponseDTO;
import com.prueba.banco.exception.AccoundNotFoundExcepcion;
import com.prueba.banco.exception.GlobalExceptionHandler;
import com.prueba.banco.exception.InsufficientBalanceException;
import com.prueba.banco.models.ENUMS.TypeTransaction;
import com.prueba.banco.service.TransactionService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private TransactionResponseDTO responseDTO;

    private static final String CONSIGNACION_JSON = """
            {
                "typeTransaction": "CONSIGNACION",
                "targetAccountNumber": 5312345678,
                "amount": 200000
            }
            """;

    private static final String RETIRO_JSON = """
            {
                "typeTransaction": "RETIRO",
                "sourceAccountNumber": 5312345678,
                "amount": 999999
            }
            """;

    private static final String CONSIGNACION_NOT_FOUND_JSON = """
            {
                "typeTransaction": "CONSIGNACION",
                "targetAccountNumber": 9999999999,
                "amount": 1000
            }
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new TransactionResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTypeTransaction(TypeTransaction.CONSIGNACION);
        responseDTO.setAmount(new BigDecimal("200000"));
        responseDTO.setTargetAccountNumber(5312345678L);
    }

    @Test
    void createTransaction_consignacion_returns201() throws Exception {
        when(transactionService.processTransaction(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CONSIGNACION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.typeTransaction").value("CONSIGNACION"));
    }

    @Test
    void createTransaction_insufficientBalance_returns400() throws Exception {
        when(transactionService.processTransaction(any()))
                .thenThrow(new InsufficientBalanceException("5312345678", 500000, 999999));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RETIRO_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTransaction_accountNotFound_returns404() throws Exception {
        when(transactionService.processTransaction(any()))
                .thenThrow(new AccoundNotFoundExcepcion("9999999999"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CONSIGNACION_NOT_FOUND_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTransactionsByAccount_returns200() throws Exception {
        when(transactionService.getTransactionsByAccount(5312345678L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/transactions/account/5312345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(200000));
    }

    @Test
    void getTransactionsByAccount_notFound_returns404() throws Exception {
        when(transactionService.getTransactionsByAccount(9999999999L))
                .thenThrow(new AccoundNotFoundExcepcion("9999999999"));

        mockMvc.perform(get("/api/transactions/account/9999999999"))
                .andExpect(status().isNotFound());
    }
}
