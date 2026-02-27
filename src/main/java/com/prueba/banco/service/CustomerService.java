package com.prueba.banco.service;

import com.prueba.banco.DTO.customersDTOs.CustomerRequestDTO;
import com.prueba.banco.DTO.customersDTOs.CustomerResponseDTO;
import com.prueba.banco.exception.CustomerNotFoundException;
import com.prueba.banco.exception.HasProductsException;
import com.prueba.banco.exception.UnderAgeException;
import com.prueba.banco.models.Customers;
import com.prueba.banco.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {
        validateAge(request.getBirthDate());

        Customers customer = modelMapper.map(request, Customers.class);
        customer = customerRepository.save(customer);
        return mapToResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customers customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id.toString()));
        return mapToResponse(customer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO request) {
        Customers customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id.toString()));

        customer.setTypeIdentity(request.getTypeIdentity());
        customer.setNumberIdentity(request.getNumberIdentity());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setBirthDate(request.getBirthDate());

        customer = customerRepository.save(customer);
        return mapToResponse(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customers customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id.toString()));

        if (customer.getAccounts() != null && !customer.getAccounts().isEmpty()) {
            throw new HasProductsException(id.toString());
        }

        customerRepository.delete(customer);
    }

    private void validateAge(LocalDate birthDate) {
        if (birthDate == null || Period.between(birthDate, LocalDate.now()).getYears() < 18) {
            throw new UnderAgeException();
        }
    }

    private CustomerResponseDTO mapToResponse(Customers customer) {
        CustomerResponseDTO response = modelMapper.map(customer, CustomerResponseDTO.class);
        if (customer.getAccounts() != null) {
            response.setAccountNumbers(
                    customer.getAccounts().stream()
                            .map(account -> account.getNumberAccount().toString())
                            .collect(Collectors.toList())
            );
        }
        return response;
    }
}
