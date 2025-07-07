package com.example.ElectricityBilling.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ElectricityBilling.entity.Billing;
import com.example.ElectricityBilling.service.BillingService;
import com.example.ElectricityBilling.service.ExcelExportService;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillingController {

    private final BillingService billingService;
    private final ExcelExportService excelExportService;

    @Autowired
    public BillingController(BillingService billingService, ExcelExportService excelExportService) {
        this.billingService = billingService;
        this.excelExportService = excelExportService;
    }

    @GetMapping
    public ResponseEntity<List<Billing>> getAllBills() {
        return ResponseEntity.ok(billingService.getAllBills());
    }

    @GetMapping("/{billId}")
    public ResponseEntity<Billing> getBillById(@PathVariable Long billId) {
        return billingService.getBillById(billId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Billing>> getBillsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(billingService.getBillsByCustomerId(customerId));
    }

    @GetMapping("/customer/{customerId}/date-range")
    public ResponseEntity<List<Billing>> getBillsByCustomerIdAndDateRange(
            @PathVariable Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(billingService.getBillsByCustomerIdAndDateRange(customerId, start, end));
    }

    @PostMapping
    public ResponseEntity<Billing> createBill(@RequestBody Billing billing) {
        return ResponseEntity.ok(billingService.saveBill(billing));
    }

    @PutMapping("/{billId}/status")
    public ResponseEntity<Billing> updateBillStatus(
            @PathVariable Long billId,
            @RequestParam Billing.BillingStatus status) {
        return ResponseEntity.ok(billingService.updateBillStatus(billId, status));
    }

    @DeleteMapping("/{billId}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long billId) {
        billingService.deleteBill(billId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateBill(@RequestBody GenerateBillRequest request) {
        try {
            System.out.println("[BillingController] Generating bill request: " + request);
            Billing bill = billingService.generateBill(request.getMeterId(), request.getBillingPeriod(), request.getDueDate());
            System.out.println("[BillingController] Generated bill: " + bill);
            return ResponseEntity.ok(bill);
        } catch (Exception e) {
            System.err.println("[BillingController] Error generating bill: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportBillsToExcel(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            System.out.println("[BillingController] Exporting bills to Excel - customerId: " + customerId + 
                             ", startDate: " + startDate + ", endDate: " + endDate);
            
            byte[] excelData = excelExportService.exportBillsToExcel(customerId, startDate, endDate);
            
            // Tạo tên file
            String fileName = "HoaDon_TienDien_" + LocalDate.now().toString() + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(excelData.length);
            
            System.out.println("[BillingController] Excel export successful, file size: " + excelData.length + " bytes");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
                    
        } catch (IOException e) {
            System.err.println("[BillingController] Error exporting to Excel: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Inner class for request body
    public static class GenerateBillRequest {
        private Long meterId;
        private String billingPeriod;
        private String dueDate;

        // Getters and setters
        public Long getMeterId() {
            return meterId;
        }

        public void setMeterId(Long meterId) {
            this.meterId = meterId;
        }

        public String getBillingPeriod() {
            return billingPeriod;
        }

        public void setBillingPeriod(String billingPeriod) {
            this.billingPeriod = billingPeriod;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        @Override
        public String toString() {
            return "GenerateBillRequest{meterId=" + meterId + ", billingPeriod='" + billingPeriod + "', dueDate='" + dueDate + "'}";
        }
    }
}