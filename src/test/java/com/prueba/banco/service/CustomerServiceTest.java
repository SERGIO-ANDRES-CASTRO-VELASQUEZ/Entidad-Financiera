package com.prueba.banco.service;

import com.prueba.banco.DTO.customersDTOs.CustomerRequestDTO;
import com.prueba.banco.DTO.customersDTOs.CustomerResponseDTO;
import com.prueba.banco.exception.CustomerNotFoundException;
import com.prueba.banco.exception.HasProductsException;
import com.prueba.banco.exception.UnderAgeException;
import com.prueba.banco.models.Account;
import com.prueba.banco.models.Customers;
import com.prueba.banco.models.ENUMS.TypeIdentity;
import com.prueba.banco.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customers customer;
    private CustomerRequestDTO requestDTO;
    private CustomerResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        customer = new Customers();
        customer.setId(1L);
        customer.setTypeIdentity(TypeIdentity.CEDULA_DE_CIUDADANIA);
        customer.setNumberIdentity("1079535438");
        customer.setFirstName("Sergio");
        customer.setLastName("Castro");
        customer.setEmail("castro@email.com");
        customer.setBirthDate(LocalDate.of(2007, 3, 22));
        customer.setAccounts(new ArrayList<>());

        requestDTO = new CustomerRequestDTO();
        requestDTO.setTypeIdentity(TypeIdentity.CEDULA_DE_CIUDADANIA);
        requestDTO.setNumberIdentity("1079535438");
        requestDTO.setFirstName("Sergio");
        requestDTO.setLastName("Castro");
        requestDTO.setEmail("castro@email.com");
        requestDTO.setBirthDate(LocalDate.of(2007, 3, 22));

        responseDTO = new CustomerResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setFirstName("Sergio");
        responseDTO.setLastName("Castro");
    }

    @Test
    void createCustomer_success() {
        when(modelMapper.map(any(CustomerRequestDTO.class), eq(Customers.class))).thenReturn(customer);
        when(customerRepository.save(any(Customers.class))).thenReturn(customer);
        when(modelMapper.map(any(Customers.class), eq(CustomerResponseDTO.class))).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.createCustomer(requestDTO);

        assertNotNull(result);
        assertEquals("Sergio", result.getFirstName());
        verify(customerRepository).save(any(Customers.class));
    }

    @Test
    void createCustomer_underAge_throwsException() {
        requestDTO.setBirthDate(LocalDate.now().minusYears(10));

        assertThrows(UnderAgeException.class, () -> customerService.createCustomer(requestDTO));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void getAllCustomers_returnsList() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(modelMapper.map(any(Customers.class), eq(CustomerResponseDTO.class))).thenReturn(responseDTO);

        List<CustomerResponseDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
    }

    @Test
    void getCustomerById_found() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(modelMapper.map(any(Customers.class), eq(CustomerResponseDTO.class))).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCustomerById_notFound_throwsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(99L));
    }

    @Test
    void updateCustomer_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customers.class))).thenReturn(customer);
        when(modelMapper.map(any(Customers.class), eq(CustomerResponseDTO.class))).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.updateCustomer(1L, requestDTO);

        assertNotNull(result);
        verify(customerRepository).save(any(Customers.class));
    }

    @Test
    void deleteCustomer_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_withProducts_throwsException() {
        customer.setAccounts(List.of(new Account()));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(HasProductsException.class, () -> customerService.deleteCustomer(1L));
        verify(customerRepository, never()).delete(any());
    }
}
