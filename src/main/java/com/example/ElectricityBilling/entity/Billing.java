package com.example.ElectricityBilling.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "billing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Billing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @Column(nullable = false)
    private Double unitsConsumed;

    @Column
    private Double previousReading;

    @Column
    private Double currentReading;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String billingPeriod;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingStatus status;

    public enum BillingStatus {
        PENDING,
        PAID,
        UNPAID,
        OVERDUE
    }

    @PrePersist
    @PreUpdate
    public void calculateTotalAmount() {
        if (unitsConsumed != null && rate != null) {
            this.totalAmount = unitsConsumed * rate;
        }
        if (createdAt == null) {
            this.createdAt = LocalDate.now();
        }
    }
} 