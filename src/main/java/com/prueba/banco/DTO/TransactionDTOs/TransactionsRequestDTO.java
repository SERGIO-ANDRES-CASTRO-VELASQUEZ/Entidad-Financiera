package com.prueba.banco.DTO.TransactionDTOs;


import com.prueba.banco.models.ENUMS.TypeTransaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionsRequestDTO {

    @NotNull(message = "El tipo de transaccion que deseas hacer es obligatorio")
    private TypeTransaction typeTransaction;

    private Long sourceAccountNumber;
    private Long targetAccountNumber;

    @NotNull(message = "El monto que manejaras es obligatorio")
    @DecimalMin(value = "100", message = "El monto debe de ser mayor a 100COP")
    private BigDecimal amount;

}
