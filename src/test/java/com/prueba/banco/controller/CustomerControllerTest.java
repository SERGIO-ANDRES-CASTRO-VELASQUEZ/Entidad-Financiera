package com.prueba.banco.controller;

import com.prueba.banco.DTO.customersDTOs.CustomerResponseDTO;
import com.prueba.banco.exception.CustomerNotFoundException;
import com.prueba.banco.exception.GlobalExceptionHandler;
import com.prueba.banco.exception.HasProductsException;
import com.prueba.banco.exception.UnderAgeException;
import com.prueba.banco.models.ENUMS.TypeIdentity;
import com.prueba.banco.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private CustomerResponseDTO responseDTO;

    private static final String CUSTOMER_JSON = """
            {
                "typeIdentity": "CEDULA_DE_CIUDADANIA",
                "numberIdentity": "1079535438",
                "firstName": "Sergio",
                "lastName": "Castro",
                "email": "castro@email.com",
                "birthDate": "2007-03-22"
            }
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new CustomerResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTypeIdentity(TypeIdentity.CEDULA_DE_CIUDADANIA);
        responseDTO.setNumberIdentity("1079535438");
        responseDTO.setFirstName("Sergio");
        responseDTO.setLastName("Castro");
        responseDTO.setEmail("castro@email.com");
        responseDTO.setBirthDate(LocalDate.of(2007, 3, 22));
    }

    @Test
    void createCustomer_returns201() throws Exception {
        when(customerService.createCustomer(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUSTOMER_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Sergio"));
    }

    @Test
    void createCustomer_underAge_returns400() throws Exception {
        when(customerService.createCustomer(any())).thenThrow(new UnderAgeException());

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUSTOMER_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllCustomers_returns200() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Sergio"));
    }

    @Test
    void getCustomerById_returns200() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getCustomerById_notFound_returns404() throws Exception {
        when(customerService.getCustomerById(99L)).thenThrow(new CustomerNotFoundException("99"));

        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCustomer_returns200() throws Exception {
        when(customerService.updateCustomer(eq(1L), any())).thenReturn(responseDTO);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUSTOMER_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCustomer_returns204() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCustomer_hasProducts_returns409() throws Exception {
        doThrow(new HasProductsException("1")).when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isConflict());
    }
}
