package com.ntt.loan.dto;

import java.util.List;

public record LoanValidationResult(
    boolean eligible,
    List<String> reasons,
    Double monthlyPayment
) {}
