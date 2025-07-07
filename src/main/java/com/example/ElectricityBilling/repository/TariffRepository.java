package com.example.ElectricityBilling.repository;

import com.example.ElectricityBilling.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {
    Optional<Tariff> findFirstByValidFromLessThanEqualAndValidToGreaterThanEqualOrderByValidFromDesc(
        LocalDate date, LocalDate sameDate
    );
    boolean existsByValidFromLessThanEqualAndValidToGreaterThanEqual(
        LocalDate date, LocalDate sameDate
    );
}