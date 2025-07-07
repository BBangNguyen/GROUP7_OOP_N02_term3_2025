package com.example.ElectricityBilling.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ElectricityBilling.entity.Customer;
import com.example.ElectricityBilling.entity.Meter;
import com.example.ElectricityBilling.repository.CustomerRepository;
import com.example.ElectricityBilling.repository.MeterRepository;

@Service
public class MeterService {
    private final MeterRepository meterRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public MeterService(MeterRepository meterRepository, CustomerRepository customerRepository) {
        this.meterRepository = meterRepository;
        this.customerRepository = customerRepository;
    }

    public List<Meter> getAllMeters() {
        return meterRepository.findAll();
    }

    public Optional<Meter> getMeterById(Long meterId) {
        return meterRepository.findById(meterId);
    }

    public List<Meter> getMetersByCustomerId(Long customerId) {
        return meterRepository.findByCustomerId(customerId);
    }

    public Meter saveMeter(Meter meter) {
        return meterRepository.save(meter);
    }

    public Meter createMeter(Meter meter, Long customerId) {
        // Check if meter number already exists
        if (meterRepository.existsByMeterNumber(meter.getMeterNumber())) {
            throw new RuntimeException("Meter number already exists");
        }
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        meter.setCustomer(customer);
        meter.setCurrentReading(meter.getInitialReading()); // Set current reading to initial reading
        return meterRepository.save(meter);
    }

    public void deleteMeter(Long meterId) {
        meterRepository.deleteById(meterId);
    }
} 