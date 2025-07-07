package com.example.ElectricityBilling.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "consumption")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @Column(name = "units_consumed")
    private Double unitsConsumed; // Đổi từ unitsUsed thành unitsConsumed để match frontend

    @Column(name = "previous_reading")
    private Double previousReading; // Thêm chỉ số cũ

    @Column(name = "current_reading", nullable = false)
    private Double currentReading; // Thêm chỉ số mới

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate;

    @Column(name = "notes")
    private String notes; // Thêm ghi chú

    @PrePersist
    @PreUpdate
    public void calculateUnitsConsumed() {
        // Tính toán tiêu thụ = chỉ số mới - chỉ số cũ
        if (currentReading != null && previousReading != null) {
            this.unitsConsumed = currentReading - previousReading;
        } else if (currentReading != null && previousReading == null) {
            this.unitsConsumed = currentReading;
        }
    }
} 