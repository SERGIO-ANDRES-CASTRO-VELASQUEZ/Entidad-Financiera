package com.prueba.banco.DTO.accountDTOs;


import com.prueba.banco.models.ENUMS.TypeAccount;
import com.prueba.banco.models.ENUMS.TypeState;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponseDTO {

    private Long id;
    private TypeAccount typeAccount;
    private Long numberAccount;
    private TypeState typeState;
    private BigDecimal balance;
    private Boolean GMFexempt;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Long customerId;
    private String customerFullName;

}
