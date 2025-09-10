package com.ntt.loan.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LoanValidationResult {
    private boolean eligible;
    private List<String> reasons;
    private BigDecimal monthlyPayment;
}
