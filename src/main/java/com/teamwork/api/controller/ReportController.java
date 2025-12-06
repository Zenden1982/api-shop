package com.teamwork.api.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Отчёты для бухгалтерии")
public class ReportController {

    private final ReportService reportService;

    @GetMapping(value = "/sales", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") // <--
                                                                                                                  // Тип
                                                                                                                  // для
                                                                                                                  // Excel
    // @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    // @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Выгрузка продаж для бухгалтерии в Excel", description = "Отчёт по оплачённым заказам за период в формате Excel.")
    public void getSalesReport(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            HttpServletResponse response) throws IOException {

        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Disposition", "attachment; filename=\"sales-report.xlsx\"");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        reportService.writeSalesReportExcel(from, to, response.getOutputStream());
    }
}
