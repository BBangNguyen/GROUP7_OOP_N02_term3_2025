package com.example.ElectricityBilling.mapper;

import com.example.ElectricityBilling.dto.CustomerRegistrationDTO;
import com.example.ElectricityBilling.dto.CustomerResponseDTO;
import com.example.ElectricityBilling.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRegistrationDTO dto) {
        Customer customer = new Customer();
        customer.setFullName(dto.getFullName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setPassword(dto.getPassword());
        customer.setLocation(dto.getLocation());
        return customer;
    }

    public CustomerResponseDTO toResponseDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setLocation(customer.getLocation());
        return dto;
    }
} 