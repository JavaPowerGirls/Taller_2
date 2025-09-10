package com.ntt.loan.controller;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.dto.LoanValidationResult;
import com.ntt.loan.service.LoanValidationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/loans")

public class LoanValidationController {

    private final LoanValidationService loanValidationService;

    public LoanValidationController(LoanValidationService loanValidationService) {
        this.loanValidationService = loanValidationService;
    }

    //implementar el post "loan-validation" recibe  @RequestBody loanvalidationrequest por parametro

    @PostMapping("/validate")
    public Mono<LoanValidationResult> validateLoan(@Valid @RequestBody LoanValidationRequest request) {
        return Mono.just(Objects.requireNonNull(loanValidationService.validateLoan(request).block()));
    }
}