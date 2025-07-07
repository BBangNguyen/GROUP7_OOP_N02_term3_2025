package com.example.ElectricityBilling.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ElectricityBilling.entity.Billing;

@Service
public class ExcelExportService {

    @Autowired
    private BillingService billingService;

    public byte[] exportBillsToExcel(Long customerId, LocalDate startDate, LocalDate endDate) throws IOException {
        List<Billing> bills;
        
        // Lấy dữ liệu hóa đơn dựa trên tham số
        if (customerId != null && startDate != null && endDate != null) {
            bills = billingService.getBillsByCustomerIdAndDateRange(customerId, startDate, endDate);
        } else if (customerId != null) {
            bills = billingService.getBillsByCustomerId(customerId);
        } else {
            bills = billingService.getAllBills();
        }

        return createExcelFile(bills, startDate, endDate);
    }

    private byte[] createExcelFile(List<Billing> bills, LocalDate startDate, LocalDate endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo Hóa đơn Điện");

        // Tạo các style
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle summaryStyle = createSummaryStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        int rowNum = 0;

        // Tiêu đề chính
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO HÓA ĐƠN TIỀN ĐIỆN");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

        // Thông tin báo cáo
        rowNum++;
        Row infoRow1 = sheet.createRow(rowNum++);
        Cell infoCell1 = infoRow1.createCell(0);
        String period = "";
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            period = "Từ ngày " + startDate.format(formatter) + " đến " + endDate.format(formatter);
        } else {
            period = "Tất cả thời gian";
        }
        infoCell1.setCellValue("Thời gian: " + period);
        infoCell1.setCellStyle(dataStyle);

        Row infoRow2 = sheet.createRow(rowNum++);
        Cell infoCell2 = infoRow2.createCell(0);
        infoCell2.setCellValue("Ngày xuất báo cáo: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        infoCell2.setCellStyle(dataStyle);

        Row infoRow3 = sheet.createRow(rowNum++);
        Cell infoCell3 = infoRow3.createCell(0);
        infoCell3.setCellValue("Tổng số hóa đơn: " + bills.size());
        infoCell3.setCellStyle(dataStyle);

        // Dòng trống
        rowNum++;

        // Header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {
            "STT", "Mã HĐ", "Khách hàng", "Số đồng hồ", "Kỳ hóa đơn", 
            "Chỉ số cũ (kWh)", "Chỉ số mới (kWh)", "Tiêu thụ (kWh)", "Đơn giá (VNĐ)", 
            "Thành tiền (VNĐ)", "Hạn thanh toán", "Trạng thái"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Dữ liệu
        int stt = 1;
        double totalAmount = 0;
        double totalConsumption = 0;

        for (Billing bill : bills) {
            Row row = sheet.createRow(rowNum++);
            
            // STT
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(stt++);
            cell0.setCellStyle(numberStyle);

            // Mã HĐ
            Cell cell1 = row.createCell(1);
            cell1.setCellValue("HĐ" + String.format("%06d", bill.getId()));
            cell1.setCellStyle(dataStyle);

            // Khách hàng
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(bill.getCustomer() != null ? bill.getCustomer().getFullName() : "N/A");
            cell2.setCellStyle(dataStyle);

            // Số đồng hồ
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(bill.getMeter() != null ? bill.getMeter().getMeterNumber() : "N/A");
            cell3.setCellStyle(dataStyle);

            // Kỳ hóa đơn
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(bill.getBillingPeriod() != null ? bill.getBillingPeriod() : "N/A");
            cell4.setCellStyle(dataStyle);

            // Chỉ số cũ
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(bill.getPreviousReading() != null ? bill.getPreviousReading().doubleValue() : 0.0);
            cell5.setCellStyle(numberStyle);

            // Chỉ số mới
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(bill.getCurrentReading() != null ? bill.getCurrentReading().doubleValue() : 0.0);
            cell6.setCellStyle(numberStyle);

            // Tiêu thụ
            Cell cell7 = row.createCell(7);
            double consumption = bill.getUnitsConsumed() != null ? bill.getUnitsConsumed().doubleValue() : 0.0;
            cell7.setCellValue(consumption);
            cell7.setCellStyle(numberStyle);
            totalConsumption += consumption;

            // Đơn giá
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(bill.getRate() != null ? bill.getRate().doubleValue() : 0.0);
            cell8.setCellStyle(currencyStyle);

            // Thành tiền
            Cell cell9 = row.createCell(9);
            double amount = bill.getTotalAmount() != null ? bill.getTotalAmount().doubleValue() : 0.0;
            cell9.setCellValue(amount);
            cell9.setCellStyle(currencyStyle);
            totalAmount += amount;

            // Hạn thanh toán
            Cell cell10 = row.createCell(10);
            if (bill.getDueDate() != null) {
                cell10.setCellValue(bill.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                cell10.setCellValue("N/A");
            }
            cell10.setCellStyle(dateStyle);

            // Trạng thái
            Cell cell11 = row.createCell(11);
            String status = getStatusInVietnamese(bill.getStatus());
            cell11.setCellValue(status);
            cell11.setCellStyle(dataStyle);
        }

        // Tổng kết
        if (!bills.isEmpty()) {
            rowNum++;
            Row totalRow = sheet.createRow(rowNum);
            
            Cell totalLabelCell = totalRow.createCell(6);
            totalLabelCell.setCellValue("TỔNG CỘNG:");
            totalLabelCell.setCellStyle(headerStyle);

            Cell totalConsumptionCell = totalRow.createCell(7);
            totalConsumptionCell.setCellValue(totalConsumption);
            totalConsumptionCell.setCellStyle(summaryStyle);

            Cell totalAmountCell = totalRow.createCell(9);
            totalAmountCell.setCellValue(totalAmount);
            totalAmountCell.setCellStyle(summaryStyle);
            
            // Thêm thống kê chi tiết
            rowNum += 2;
            Row statsHeaderRow = sheet.createRow(rowNum++);
            Cell statsHeaderCell = statsHeaderRow.createCell(0);
            statsHeaderCell.setCellValue("THỐNG KÊ CHI TIẾT:");
            statsHeaderCell.setCellStyle(headerStyle);
            
            // Thống kê theo trạng thái
            long paidCount = bills.stream().filter(b -> b.getStatus() == Billing.BillingStatus.PAID).count();
            long pendingCount = bills.stream().filter(b -> b.getStatus() == Billing.BillingStatus.PENDING).count();
            long overdueCount = bills.stream().filter(b -> b.getStatus() == Billing.BillingStatus.OVERDUE).count();
            
            Row stat1Row = sheet.createRow(rowNum++);
            stat1Row.createCell(0).setCellValue("- Số hóa đơn đã thanh toán:");
            stat1Row.createCell(1).setCellValue(paidCount + " hóa đơn");
            
            Row stat2Row = sheet.createRow(rowNum++);
            stat2Row.createCell(0).setCellValue("- Số hóa đơn chờ thanh toán:");
            stat2Row.createCell(1).setCellValue(pendingCount + " hóa đơn");
            
            Row stat3Row = sheet.createRow(rowNum++);
            stat3Row.createCell(0).setCellValue("- Số hóa đơn quá hạn:");
            stat3Row.createCell(1).setCellValue(overdueCount + " hóa đơn");
            
            Row stat4Row = sheet.createRow(rowNum++);
            stat4Row.createCell(0).setCellValue("- Tiền điện trung bình/hóa đơn:");
            stat4Row.createCell(1).setCellValue(String.format("%,.0f VNĐ", totalAmount / bills.size()));
            
            Row stat5Row = sheet.createRow(rowNum++);
            stat5Row.createCell(0).setCellValue("- Tiêu thụ điện trung bình/hóa đơn:");
            stat5Row.createCell(1).setCellValue(String.format("%,.1f kWh", totalConsumption / bills.size()));
        }

        // Tự động điều chỉnh độ rộng cột
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Xuất file
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private String getStatusInVietnamese(Billing.BillingStatus status) {
        if (status == null) return "N/A";
        
        switch (status) {
            case PAID:
                return "Đã thanh toán";
            case PENDING:
                return "Chờ thanh toán";
            case UNPAID:
                return "Chưa thanh toán";
            case OVERDUE:
                return "Quá hạn";
            default:
                return status.toString();
        }
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0\" VNĐ\""));
        return style;
    }

    private CellStyle createSummaryStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0\" VNĐ\""));
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
