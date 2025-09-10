package com.ntt.loan.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
public class LoanValidation {

    private boolean eligible;
    private List<LoanValidationReason> reasons;
    private Double monthlyPayment;
    private Double monthlySalary;
    private Double requestedAmount;
    private Integer termMonths;
    private LocalDate lastLoanDate;
    private LocalDate validationDate;
}
