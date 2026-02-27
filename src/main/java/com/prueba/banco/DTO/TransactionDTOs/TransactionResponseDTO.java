package com.prueba.banco.DTO.TransactionDTOs;


import com.prueba.banco.models.ENUMS.TypeTransaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponseDTO {

    private Long id;
    private TypeTransaction typeTransaction;
    private BigDecimal amount;
    private LocalDateTime createdDate;
    private Long sourceAccountNumber;
    private Long targetAccountNumber;

}
