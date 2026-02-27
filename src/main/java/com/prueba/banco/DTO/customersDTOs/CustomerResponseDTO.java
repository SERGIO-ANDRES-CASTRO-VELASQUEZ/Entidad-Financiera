package com.prueba.banco.DTO.customersDTOs;

import com.prueba.banco.models.ENUMS.TypeIdentity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerResponseDTO {

    private Long id;
    private TypeIdentity typeIdentity;
    private String numberIdentity;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private List<String> accountNumbers;


}
