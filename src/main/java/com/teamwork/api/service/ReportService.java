package com.teamwork.api.service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.teamwork.api.model.DTO.SalesReportRowDTO;
import com.teamwork.api.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;

    /**
     * Записывает отчет в Excel файл
     */
    public void writeSalesReportExcel(LocalDate from, LocalDate to, OutputStream outputStream) throws IOException {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay().minusSeconds(1);

        List<SalesReportRowDTO> rows = orderRepository.findSalesReport(fromDt, toDt);

        // Создаем новую рабочую книгу Excel
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Отчет о продажах");

            // --- Оформление заголовка ---
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Создаем заголовки
            Cell cell1 = headerRow.createCell(0);
            cell1.setCellValue("Наименование");
            cell1.setCellStyle(headerStyle);

            Cell cell2 = headerRow.createCell(1);
            cell2.setCellValue("Количество");
            cell2.setCellStyle(headerStyle);

            Cell cell3 = headerRow.createCell(2);
            cell3.setCellValue("Входящая цена");
            cell3.setCellStyle(headerStyle);

            // Устанавливаем ширину колонок
            sheet.setColumnWidth(0, 30 * 256); // Наименование
            sheet.setColumnWidth(1, 15 * 256); // Количество
            sheet.setColumnWidth(2, 15 * 256); // Цена

            // --- Заполняем данные ---
            int rowNum = 1;
            for (SalesReportRowDTO r : rows) {
                BigDecimal wholesalePrice = calculateWholesalePrice(r.getPrice(), r.getQuantity());

                String fullName = r.getProductName();
                if (r.getHeight() != null && r.getWidth() != null) {
                    fullName = fullName + " " + r.getHeight() + "x" + r.getWidth();
                }

                Row dataRow = sheet.createRow(rowNum);
                dataRow.createCell(0).setCellValue(fullName);
                dataRow.createCell(1).setCellValue(r.getQuantity());
                dataRow.createCell(2).setCellValue(wholesalePrice.doubleValue());

                rowNum++;
            }

            // Записываем в выходной поток
            workbook.write(outputStream);
        }
    }

    private BigDecimal calculateWholesalePrice(BigDecimal basePrice, Integer quantity) {
        if (basePrice == null)
            return BigDecimal.ZERO;
        if (quantity == null)
            quantity = 0;

        BigDecimal discountPercent = BigDecimal.ZERO;

        if (quantity >= 100) {
            discountPercent = new BigDecimal("0.20");
        } else if (quantity >= 50) {
            discountPercent = new BigDecimal("0.10");
        } else if (quantity >= 10) {
            discountPercent = new BigDecimal("0.05");
        }

        BigDecimal multiplier = BigDecimal.ONE.subtract(discountPercent);
        return basePrice.multiply(multiplier);
    }
}
