package com.prueba.banco.DTO.accountDTOs;


import com.prueba.banco.models.ENUMS.TypeAccount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequestDTO {

    @NotNull(message = "El tipo de cuenta a Escoger es obligatoria")
    private TypeAccount typeAccount;

    @NotNull(message = "El id del cliete al que se asociara la cuenta es obligatorio")
    private Long customerId;

    private BigDecimal balance = BigDecimal.ZERO;

    private Boolean GMFexempt = false;
}
