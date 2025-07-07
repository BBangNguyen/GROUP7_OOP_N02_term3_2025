package com.example.ElectricityBilling.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ElectricityBilling.entity.Meter;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long> {
    List<Meter> findByCustomerId(Long customerId);
    Optional<Meter> findByIdAndCustomerId(Long id, Long customerId);
    boolean existsByCustomerId(Long customerId);
    boolean existsByMeterNumber(String meterNumber);
} 