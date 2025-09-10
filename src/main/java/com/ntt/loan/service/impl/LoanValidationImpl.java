package com.ntt.loan.service.impl;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.model.LoanValidation;
import com.ntt.loan.service.LoanValidationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LoanValidationImpl implements LoanValidationService {

    @Override
    public Mono<LoanValidation> validateLoan(LoanValidationRequest request) {
        return null;
    }
}
