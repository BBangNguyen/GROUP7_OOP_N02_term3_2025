package com.example.ElectricityBilling.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ElectricityBilling.entity.Tariff;
import com.example.ElectricityBilling.repository.TariffRepository;

@Service
public class TariffService {
    private final TariffRepository tariffRepository;

    @Autowired
    public TariffService(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    public List<Tariff> getAllTariffs() {
        return tariffRepository.findAll();
    }

    public Optional<Tariff> getTariffById(Long id) {
        return tariffRepository.findById(id);
    }

    public Tariff saveTariff(Tariff tariff) {
        return tariffRepository.save(tariff);
    }

    public void deleteTariff(Long id) {
        tariffRepository.deleteById(id);
    }

    public Optional<Tariff> getCurrentTariff(LocalDate date) {
        return tariffRepository.findFirstByValidFromLessThanEqualAndValidToGreaterThanEqualOrderByValidFromDesc(date, date);
    }

    public void initSampleTariffs() {
        // Chỉ tạo dữ liệu mẫu nếu chưa có tariff nào
        if (tariffRepository.count() == 0) {
            // Biểu giá sinh hoạt
            Tariff domesticTariff = new Tariff();
            domesticTariff.setRatePerUnit(2500.0); // 2,500 VND/kWh
            domesticTariff.setValidFrom(LocalDate.of(2024, 1, 1));
            domesticTariff.setValidTo(LocalDate.of(2024, 12, 31));
            tariffRepository.save(domesticTariff);

            // Biểu giá sản xuất
            Tariff industrialTariff = new Tariff();
            industrialTariff.setRatePerUnit(3200.0); // 3,200 VND/kWh
            industrialTariff.setValidFrom(LocalDate.of(2024, 1, 1));
            industrialTariff.setValidTo(LocalDate.of(2024, 12, 31));
            tariffRepository.save(industrialTariff);

            // Biểu giá thương mại
            Tariff commercialTariff = new Tariff();
            commercialTariff.setRatePerUnit(2800.0); // 2,800 VND/kWh
            commercialTariff.setValidFrom(LocalDate.of(2024, 1, 1));
            commercialTariff.setValidTo(LocalDate.of(2024, 12, 31));
            tariffRepository.save(commercialTariff);

            // Biểu giá mới cho năm 2025
            Tariff newTariff2025 = new Tariff();
            newTariff2025.setRatePerUnit(2750.0); // 2,750 VND/kWh
            newTariff2025.setValidFrom(LocalDate.of(2025, 1, 1));
            newTariff2025.setValidTo(LocalDate.of(2025, 12, 31));
            tariffRepository.save(newTariff2025);
        }
    }
}