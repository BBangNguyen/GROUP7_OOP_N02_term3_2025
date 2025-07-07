package com.example.ElectricityBilling.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ElectricityBilling.entity.Consumption;
import com.example.ElectricityBilling.entity.Meter;
import com.example.ElectricityBilling.repository.ConsumptionRepository;
import com.example.ElectricityBilling.repository.MeterRepository;

@Service
public class ConsumptionService {
    private final ConsumptionRepository consumptionRepository;
    private final MeterRepository meterRepository;

    @Autowired
    public ConsumptionService(ConsumptionRepository consumptionRepository, MeterRepository meterRepository) {
        this.consumptionRepository = consumptionRepository;
        this.meterRepository = meterRepository;
    }

    public List<Consumption> getAllConsumptions() {
        return consumptionRepository.findAll();
    }

    public Optional<Consumption> getConsumptionById(Long id) {
        return consumptionRepository.findById(id);
    }

    public List<Consumption> getConsumptionsByMeterId(Long meterId) {
        return consumptionRepository.findByMeter_Id(meterId);
    }

    public List<Consumption> getConsumptionsByMeterIdAndYear(Long meterId, Integer year) {
        return consumptionRepository.findByMeter_IdAndYear(meterId, year);
    }

    public Consumption saveConsumption(Consumption consumption) {
        // Tìm consumption trước đó để lấy previousReading
        if (consumption.getMeter() != null) {
            Optional<Consumption> lastConsumption = consumptionRepository
                    .findFirstByMeter_IdOrderByRecordedDateDesc(consumption.getMeter().getId());
            
            if (lastConsumption.isPresent()) {
                consumption.setPreviousReading(lastConsumption.get().getCurrentReading());
            } else {
                // Nếu không có consumption trước đó, lấy initialReading từ meter
                consumption.setPreviousReading(consumption.getMeter().getInitialReading());
            }

            // Set month và year từ recordedDate
            LocalDate recordedDate = consumption.getRecordedDate();
            if (recordedDate != null) {
                consumption.setMonth(recordedDate.getMonthValue());
                consumption.setYear(recordedDate.getYear());
            }
        }

        return consumptionRepository.save(consumption);
    }

    public void deleteConsumption(Long id) {
        consumptionRepository.deleteById(id);
    }

    public Consumption createConsumptionFromRequest(Long meterId, Double currentReading, String readingDate, String notes) {
        // Tìm meter theo ID
        Optional<Meter> meterOpt = meterRepository.findById(meterId);
        if (meterOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đồng hồ với ID: " + meterId);
        }

        Meter meter = meterOpt.get();
        
        // Tạo consumption mới
        Consumption consumption = new Consumption();
        consumption.setMeter(meter);
        consumption.setCurrentReading(currentReading);
        consumption.setRecordedDate(LocalDate.parse(readingDate));
        consumption.setNotes(notes);

        return saveConsumption(consumption);
    }
} 