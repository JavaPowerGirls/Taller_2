package com.ntt.loan.controller;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.dto.LoanValidationResult;
import com.ntt.loan.service.LoanValidationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
//@RequestMapping(value = "/api/v1/loans")
public class LoanValidationController {

    private final LoanValidationService loanValidationService;

    public LoanValidationController(LoanValidationService loanValidationService) {
        this.loanValidationService = loanValidationService;
    }

    // endpoint principal que recibe el request y valida el prestamo
    @PostMapping("/loan-validations")
    public Mono<LoanValidationResult> validateLoan(@Valid @RequestBody LoanValidationRequest request) {
        // llamar al servicio - los errores los maneja el GlobalExceptionHandler
        return loanValidationService.validateLoan(request);
    }
}