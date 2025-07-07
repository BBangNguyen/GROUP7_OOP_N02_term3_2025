package com.example.ElectricityBilling.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CreateMeterRequest {
    private String meterNumber;
    private String meterType;
    private LocalDate installationDate;
    private Double initialReading;
    private Long customerId;
}
