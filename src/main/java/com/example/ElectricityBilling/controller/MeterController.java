package com.example.ElectricityBilling.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ElectricityBilling.dto.CreateMeterRequest;
import com.example.ElectricityBilling.entity.Meter;
import com.example.ElectricityBilling.service.MeterService;

@RestController
@RequestMapping("/api/meters")
@CrossOrigin(origins = "*")
public class MeterController {

    private final MeterService meterService;

    @Autowired
    public MeterController(MeterService meterService) {
        this.meterService = meterService;
    }

    @GetMapping
    public ResponseEntity<List<Meter>> getAllMeters() {
        return ResponseEntity.ok(meterService.getAllMeters());
    }

    @GetMapping("/{meterId}")
    public ResponseEntity<Meter> getMeterById(@PathVariable Long meterId) {
        return meterService.getMeterById(meterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Meter>> getMetersByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(meterService.getMetersByCustomerId(customerId));
    }

    @PostMapping
    public ResponseEntity<?> createMeter(@RequestBody CreateMeterRequest request) {
        try {
            Meter meter = new Meter();
            meter.setMeterNumber(request.getMeterNumber());
            meter.setMeterType(Meter.MeterType.valueOf(request.getMeterType()));
            meter.setInstallationDate(request.getInstallationDate());
            meter.setInitialReading(request.getInitialReading());
            
            Meter savedMeter = meterService.createMeter(meter, request.getCustomerId());
            return ResponseEntity.ok(savedMeter);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid meter type: " + request.getMeterType());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{meterId}")
    public ResponseEntity<Void> deleteMeter(@PathVariable Long meterId) {
        meterService.deleteMeter(meterId);
        return ResponseEntity.ok().build();
    }
} 