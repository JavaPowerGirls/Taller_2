package com.ntt.loan.model;

import lombok.Builder;
import lombok.Data;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;


@Data
@Builder
public class LoanValidation {

    private boolean eligible;
    private List<LoanValidationReason> reasons;
    private Double monthlyPayment;
    private Double monthlySalary;
    private Double requestedAmount;
    private Integer termMonths;
    private LocalDate lastLoanDate;

    public boolean hasRecentLoans(Clock clock) {
        return this.lastLoanDate != null &&
                !this.lastLoanDate.isBefore(LocalDate.now(clock).minusMonths(3));
    }

    public boolean isValidTerm() {
        return this.termMonths >= 1 && this.termMonths <= 36;
    }

    public boolean hasPaymentCapacity() {
        return this.monthlyPayment <= this.monthlySalary * 0.40;
    }

    public boolean hasValidData() {
        return this.monthlySalary > 0 && this.requestedAmount > 0 && this.termMonths > 0;
    }
}
