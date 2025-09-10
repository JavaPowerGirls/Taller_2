package com.ntt.loan.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanValidation {
    private boolean valid;
    private String message;
}