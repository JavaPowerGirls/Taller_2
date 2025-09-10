package com.ntt.loan.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanValidationRequest {
    private BigDecimal monthlySalary;
    private BigDecimal requestedAmount;
    private Integer termMonths;
    private LocalDate lastLoanDate;
}
