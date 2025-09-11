package com.ntt.loan.controller;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.dto.LoanValidationResult;
import com.ntt.loan.service.LoanValidationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(LoanValidationController.class)

class LoanValidationControllerTest {
    // Spring inyecta automáticamente un WebTestClient configurado
    @Autowired
    private WebTestClient webTestClient;

    // MockBean registra un bean simulado en el contexto de Spring
    @Autowired
    private LoanValidationService loanValidationService;
    static class TestConfig {
        @Bean
        public LoanValidationService loanValidationService() {
            return Mockito.mock(LoanValidationService.class);
        }
    }

    @Test
    void testValidateLoanApproved() {
        LoanValidationResult expectedResult = new LoanValidationResult(true, "Loan approved");

        Mockito.when(loanValidationService.validateLoan(any(LoanValidationRequest.class)))
                .thenReturn(Mono.just(expectedResult));

        LoanValidationRequest request = new LoanValidationRequest();
        request.setMonthlySalary(BigDecimal.valueOf(3000));
        request.setRequestedAmount(BigDecimal.valueOf(10000));
        request.setTermMonths(12);
        request.setLastLoanDate(LocalDate.now().minusMonths(6));

        webTestClient.post()
                .uri("/api/v1/loans/validate")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoanValidationResult.class)
                .isEqualTo(expectedResult);
    }

    @Test
    void testValidateLoanRejected() {
        LoanValidationResult expectedResult = new LoanValidationResult(false, "Requested amount too high");

        Mockito.when(loanValidationService.validateLoan(any(LoanValidationRequest.class)))
                .thenReturn(Mono.just(expectedResult));

        LoanValidationRequest request = new LoanValidationRequest();
        request.setMonthlySalary(BigDecimal.valueOf(1000));
        request.setRequestedAmount(BigDecimal.valueOf(20000));
        request.setTermMonths(12);
        request.setLastLoanDate(LocalDate.now().minusMonths(6));

        webTestClient.post()
                .uri("/api/v1/loans/validate")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoanValidationResult.class)
                .isEqualTo(expectedResult);
    }
}