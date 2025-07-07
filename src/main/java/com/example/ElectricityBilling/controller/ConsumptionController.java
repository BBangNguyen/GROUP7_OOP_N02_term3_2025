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

import com.example.ElectricityBilling.entity.Consumption;
import com.example.ElectricityBilling.service.ConsumptionService;

@RestController
@RequestMapping("/api/consumptions")
@CrossOrigin(origins = "*")
public class ConsumptionController {

    private final ConsumptionService consumptionService;

    @Autowired
    public ConsumptionController(ConsumptionService consumptionService) {
        this.consumptionService = consumptionService;
    }

    @GetMapping
    public ResponseEntity<List<Consumption>> getAllConsumptions() {
        return ResponseEntity.ok(consumptionService.getAllConsumptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consumption> getConsumptionById(@PathVariable Long id) {
        return consumptionService.getConsumptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/meter/{meterId}")
    public ResponseEntity<List<Consumption>> getConsumptionsByMeterId(@PathVariable Long meterId) {
        return ResponseEntity.ok(consumptionService.getConsumptionsByMeterId(meterId));
    }

    @GetMapping("/meter/{meterId}/year/{year}")
    public ResponseEntity<List<Consumption>> getConsumptionsByMeterIdAndYear(
            @PathVariable Long meterId,
            @PathVariable Integer year) {
        return ResponseEntity.ok(consumptionService.getConsumptionsByMeterIdAndYear(meterId, year));
    }

    @PostMapping
    public ResponseEntity<?> createConsumption(@RequestBody ConsumptionRequest request) {
        try {
            System.out.println("[ConsumptionController] Creating consumption: " + request);
            Consumption consumption = consumptionService.createConsumptionFromRequest(
                request.getMeterId(), 
                request.getCurrentReading(), 
                request.getReadingDate(), 
                request.getNotes()
            );
            System.out.println("[ConsumptionController] Created consumption: " + consumption);
            return ResponseEntity.ok(consumption);
        } catch (Exception e) {
            System.err.println("[ConsumptionController] Error creating consumption: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Inner class để nhận dữ liệu từ frontend
    public static class ConsumptionRequest {
        private Long meterId;
        private Double currentReading;
        private String readingDate;
        private String notes;

        // Getters and setters
        public Long getMeterId() { return meterId; }
        public void setMeterId(Long meterId) { this.meterId = meterId; }
        public Double getCurrentReading() { return currentReading; }
        public void setCurrentReading(Double currentReading) { this.currentReading = currentReading; }
        public String getReadingDate() { return readingDate; }
        public void setReadingDate(String readingDate) { this.readingDate = readingDate; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        @Override
        public String toString() {
            return "ConsumptionRequest{meterId=" + meterId + ", currentReading=" + currentReading + 
                   ", readingDate='" + readingDate + "', notes='" + notes + "'}";
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsumption(@PathVariable Long id) {
        consumptionService.deleteConsumption(id);
        return ResponseEntity.ok().build();
    }
} 