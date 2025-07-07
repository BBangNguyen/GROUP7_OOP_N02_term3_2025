package com.example.ElectricityBilling.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ElectricityBilling.entity.Consumption;

@Repository
public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {
    List<Consumption> findByMeter_Id(Long meterId);
    List<Consumption> findByMeter_IdAndMonthAndYear(Long meterId, Integer month, Integer year);
    Optional<Consumption> findFirstByMeter_IdOrderByRecordedDateDesc(Long meterId);
    List<Consumption> findByMeter_IdAndYear(Long meterId, Integer year);
}