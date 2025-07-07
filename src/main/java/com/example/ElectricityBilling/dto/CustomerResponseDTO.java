package com.example.ElectricityBilling.dto;

import lombok.Data;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String location;
} 