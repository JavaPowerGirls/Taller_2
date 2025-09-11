package com.ntt.loan.controller;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.dto.LoanValidationResult;
import com.ntt.loan.service.LoanValidationService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1/loans")

public class LoanValidationController {

    private final LoanValidationService loanValidationService;

    public LoanValidationController(LoanValidationService loanValidationService) {
        this.loanValidationService = loanValidationService;
    }

    //implementar el post "loan-validation" recibe  @RequestBody loanvalidationrequest por parametro

    @PostMapping("/validate")
    public Mono<LoanValidationResult> validateLoan(@RequestBody LoanValidationRequest request) {
        return loanValidationService.validateLoan(request);
    }
}