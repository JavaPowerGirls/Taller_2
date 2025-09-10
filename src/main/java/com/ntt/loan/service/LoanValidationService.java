package com.ntt.loan.service;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.dto.LoanValidationResult;
import reactor.core.publisher.Mono;

public interface LoanValidationService {

    Mono<LoanValidationResult> validateLoan(LoanValidationRequest request);
}
