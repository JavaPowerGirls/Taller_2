package com.ntt.loan.service;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.model.LoanValidation;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

public interface LoanValidationService {
    public Mono<LoanValidation> validateLoan(LoanValidationRequest request);

}
