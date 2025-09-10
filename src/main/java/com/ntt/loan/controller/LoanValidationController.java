package com.ntt.loan.controller;

import com.ntt.loan.service.LoanValidationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LoanValidationController {

    private final LoanValidationService loanValidationService;


    public LoanValidationController(LoanValidationService loanValidationService) {
        this.loanValidationService = loanValidationService;
    }

    //implementar el post "loan-validation" recibe  @RequestBody loanvalidationrequest por parametro





}
