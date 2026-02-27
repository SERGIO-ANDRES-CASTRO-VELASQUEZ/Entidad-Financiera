package com.prueba.banco.DTO.customersDTOs;


import com.prueba.banco.models.ENUMS.TypeIdentity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRequestDTO {

    @NotNull(message = "Tu tipo de identificación es obligatorio")
    private TypeIdentity typeIdentity;

    @NotBlank(message = "Tu numero de identificacion es obligatorio")
    private String numberIdentity;

    @NotBlank(message = "Tu nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "Tu apellido es obligatorio")
    private String lastName;

    @Email(message = "El formato de correo que ingresaste es invalido")
    @NotBlank(message = "Tu correo es obligatorio")
    private String email;

    private LocalDate birthDate;


}
