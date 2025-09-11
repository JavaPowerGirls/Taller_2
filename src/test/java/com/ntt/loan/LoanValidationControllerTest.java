package com.ntt.loan;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.ntt.loan.controller.LoanValidationController;
import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.dto.LoanValidationResult;
import com.ntt.loan.service.LoanValidationService;

import reactor.core.publisher.Mono;

@WebFluxTest(LoanValidationController.class)
class LoanValidationControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LoanValidationService loanValidationService;

    @Test
    void testValidateLoanApproved() {
        // caso aprobado
        LoanValidationResult expectedResult = new LoanValidationResult(
                true,
                Collections.emptyList(),
                416.67 // 10000/24
        );

        when(loanValidationService.validateLoan(any(LoanValidationRequest.class)))
                .thenReturn(Mono.just(expectedResult));

        LoanValidationRequest request = new LoanValidationRequest(
                3000.0,
                10000.0, 
                24,
                null
        );

        webTestClient.post()
                .uri("/loan-validations")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoanValidationResult.class)
                .value(result -> {
                    assert result.eligible();
                    assert Math.abs(result.monthlyPayment() - 416.67) < 0.01;
                });
    }

    @Test
    void testValidateLoanRejected() {
        // caso rechazado - capacidad insuficiente
        LoanValidationResult expectedResult = new LoanValidationResult(
                false,
                List.of("CAPACIDAD_INSUFICIENTE"),
                null
        );

        when(loanValidationService.validateLoan(any(LoanValidationRequest.class)))
                .thenReturn(Mono.just(expectedResult));

        LoanValidationRequest request = new LoanValidationRequest(
                1000.0,
                15000.0,
                24, 
                null
        );

        webTestClient.post()
                .uri("/loan-validations")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoanValidationResult.class)
                .value(result -> {
                    assert !result.eligible();
                    assert result.reasons().contains("CAPACIDAD_INSUFICIENTE");
                });
    }
}