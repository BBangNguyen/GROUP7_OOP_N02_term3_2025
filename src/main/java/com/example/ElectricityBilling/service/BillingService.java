package com.example.ElectricityBilling.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ElectricityBilling.entity.Billing;
import com.example.ElectricityBilling.entity.Consumption;
import com.example.ElectricityBilling.entity.Meter;
import com.example.ElectricityBilling.entity.Tariff;
import com.example.ElectricityBilling.repository.BillingRepository;
import com.example.ElectricityBilling.repository.ConsumptionRepository;
import com.example.ElectricityBilling.repository.MeterRepository;
import com.example.ElectricityBilling.repository.TariffRepository;

@Service
public class BillingService {
    private final BillingRepository billingRepository;
    private final TariffRepository tariffRepository;
    private final MeterRepository meterRepository;
    private final ConsumptionRepository consumptionRepository;

    @Autowired
    public BillingService(BillingRepository billingRepository, TariffRepository tariffRepository, 
                         MeterRepository meterRepository, ConsumptionRepository consumptionRepository) {
        this.billingRepository = billingRepository;
        this.tariffRepository = tariffRepository;
        this.meterRepository = meterRepository;
        this.consumptionRepository = consumptionRepository;
    }

    public List<Billing> getAllBills() {
        return billingRepository.findAll();
    }

    public Optional<Billing> getBillById(Long billId) {
        return billingRepository.findById(billId);
    }

    public List<Billing> getBillsByCustomerId(Long customerId) {
        return billingRepository.findByCustomerId(customerId);
    }

    public Billing saveBill(Billing billing) {
        // Calculate rate from current tariff if not set
        if (billing.getRate() == null) {
            Optional<Tariff> tariff = tariffRepository.findFirstByValidFromLessThanEqualAndValidToGreaterThanEqualOrderByValidFromDesc(
                billing.getCreatedAt(), billing.getCreatedAt());
            tariff.ifPresent(t -> billing.setRate(t.getRatePerUnit()));
        }
        // totalAmount is auto-calculated by entity
        return billingRepository.save(billing);
    }

    public void deleteBill(Long billId) {
        billingRepository.deleteById(billId);
    }

    public Billing updateBillStatus(Long billId, Billing.BillingStatus status) {
        Billing bill = billingRepository.findById(billId).orElseThrow();
        bill.setStatus(status);
        return billingRepository.save(bill);
    }

    public List<Billing> getBillsByCustomerIdAndDateRange(Long customerId, LocalDate start, LocalDate end) {
        return billingRepository.findByCustomerIdAndCreatedAtBetween(customerId, start, end);
    }

    public Billing generateBill(Long meterId, String billingPeriod, String dueDate) {
        try {
            System.out.println("[BillingService] Generating bill for meterId: " + meterId + ", period: " + billingPeriod + ", dueDate: " + dueDate);
            
            // Tìm meter theo ID
            Optional<Meter> meterOpt = meterRepository.findById(meterId);
            if (meterOpt.isEmpty()) {
                throw new RuntimeException("Không tìm thấy đồng hồ điện với ID: " + meterId);
            }
            
            Meter meter = meterOpt.get();
            System.out.println("[BillingService] Found meter: " + meter.getMeterNumber());
            
            // Tìm consumption gần nhất của meter này
            Optional<Consumption> consumptionOpt = consumptionRepository.findFirstByMeter_IdOrderByRecordedDateDesc(meterId);
            if (consumptionOpt.isEmpty()) {
                throw new RuntimeException("Chưa có dữ liệu tiêu thụ cho đồng hồ này");
            }
            
            Consumption latestConsumption = consumptionOpt.get();
            System.out.println("[BillingService] Latest consumption: " + latestConsumption.getUnitsConsumed() + " kWh");
            
            // Tìm tariff hiện tại
            LocalDate currentDate = LocalDate.now();
            Optional<Tariff> tariffOpt = tariffRepository.findFirstByValidFromLessThanEqualAndValidToGreaterThanEqualOrderByValidFromDesc(
                currentDate, currentDate);
            
            if (tariffOpt.isEmpty()) {
                throw new RuntimeException("Không tìm thấy biểu giá hiện tại");
            }
            
            Tariff tariff = tariffOpt.get();
            System.out.println("[BillingService] Using tariff: " + tariff.getRatePerUnit() + " per unit");
            
            // Tạo hóa đơn mới
            Billing billing = new Billing();
            billing.setCustomer(meter.getCustomer());
            billing.setMeter(meter);
            billing.setUnitsConsumed(latestConsumption.getUnitsConsumed());
            billing.setRate(tariff.getRatePerUnit());
            billing.setBillingPeriod(billingPeriod);
            billing.setDueDate(LocalDate.parse(dueDate));
            billing.setCreatedAt(LocalDate.now());
            billing.setStatus(Billing.BillingStatus.PENDING);
            
            // Lấy consumption cũ và mới để hiển thị
            List<Consumption> consumptions = consumptionRepository.findByMeter_Id(meterId);
            if (consumptions.size() >= 2) {
                billing.setPreviousReading(consumptions.get(1).getCurrentReading());
                billing.setCurrentReading(consumptions.get(0).getCurrentReading());
            } else {
                billing.setPreviousReading(0.0);
                billing.setCurrentReading(latestConsumption.getCurrentReading());
            }
            
            // totalAmount sẽ được tính tự động trong @PrePersist
            
            Billing savedBilling = billingRepository.save(billing);
            System.out.println("[BillingService] Created bill with ID: " + savedBilling.getId());
            
            return savedBilling;
            
        } catch (Exception e) {
            System.err.println("[BillingService] Error generating bill: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo hóa đơn: " + e.getMessage());
        }
    }
}