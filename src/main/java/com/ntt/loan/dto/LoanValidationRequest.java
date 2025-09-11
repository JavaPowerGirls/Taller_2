package com.ntt.loan.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LoanValidationRequest(
    @NotNull(message = "Monthly salary is required")
    Double monthlySalary,
    
    @NotNull(message = "Requested amount is required") 
    Double requestedAmount,
    
    @NotNull(message = "Term months is required")
    Integer termMonths,
    
    LocalDate lastLoanDate
) {}
