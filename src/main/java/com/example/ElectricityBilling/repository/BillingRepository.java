package com.example.ElectricityBilling.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ElectricityBilling.entity.Billing;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {
    List<Billing> findByCustomerId(Long customerId);
    List<Billing> findByCustomer_IdAndMeter_Id(Long customerId, Long meterId);
    List<Billing> findByCustomerIdAndStatus(Long customerId, Billing.BillingStatus status);
    List<Billing> findByCustomerIdAndCreatedAtBetween(Long customerId, java.time.LocalDate start, java.time.LocalDate end);
}