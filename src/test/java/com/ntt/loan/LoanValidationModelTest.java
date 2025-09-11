package com.ntt.loan;

import com.ntt.loan.model.LoanValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class LoanValidationModelTest {

    private Clock fixedClock;
    private LocalDate fixedDate;

    @BeforeEach
    void setUp() {
        fixedDate = LocalDate.of(2025, 9, 10);
        fixedClock = Clock.fixed(fixedDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    }

    @Test
    @DisplayName("Debe detectar préstamos recientes")
    void hasRecentLoans_true() {
        // R1: 2 meses atras cuenta como reciente (regla incluyente)
        LocalDate recentLoanDate = fixedDate.minusMonths(2);
        LoanValidation loan = LoanValidation.builder()
                .lastLoanDate(recentLoanDate)
                .build();

        assertTrue(loan.hasRecentLoans(fixedClock));
    }

    @Test
    @DisplayName("Sin préstamos previos no hay recientes")
    void hasRecentLoans_null() {
        // cliente nuevo sin historial de prestamos
        LoanValidation loan = LoanValidation.builder()
                .lastLoanDate(null)
                .build();

        assertFalse(loan.hasRecentLoans(fixedClock));
    }

    @Test
    @DisplayName("Préstamos antiguos no son recientes")
    void hasRecentLoans_false() {
        // R1: 4 meses ya pasa los 3 meses limite
        LocalDate oldLoanDate = fixedDate.minusMonths(4);
        LoanValidation loan = LoanValidation.builder()
                .lastLoanDate(oldLoanDate)
                .build();

        assertFalse(loan.hasRecentLoans(fixedClock));
    }

    @Test
    @DisplayName("Plazo válido es aceptado")
    void isValidTerm_true() {
        // R2: 12 meses cumple con termMonths ≤ 36 y ≥ 1
        LoanValidation loan = LoanValidation.builder()
                .termMonths(12)
                .build();

        assertTrue(loan.isValidTerm());
    }

    @Test
    @DisplayName("Plazo excesivo es rechazado")
    void isValidTerm_false() {
        // R2: 50 meses rompe la regla de maximo 36
        LoanValidation loan = LoanValidation.builder()
                .termMonths(50)
                .build();

        assertFalse(loan.isValidTerm());
    }

    @Test
    @DisplayName("Capacidad de pago suficiente es válida")
    void hasPaymentCapacity_true() {
        // R3: 300/1000 = 30% que es menor al 40% limite
        LoanValidation loan = LoanValidation.builder()
                .monthlyPayment(300.0)
                .monthlySalary(1000.0)
                .build();

        assertTrue(loan.hasPaymentCapacity());
    }

    @Test
    @DisplayName("Capacidad de pago insuficiente es inválida")
    void hasPaymentCapacity_false() {
        // R3: 500/1000 = 50% supera el 0.40 * monthlySalary
        LoanValidation loan = LoanValidation.builder()
                .monthlyPayment(500.0)
                .monthlySalary(1000.0)
                .build();

        assertFalse(loan.hasPaymentCapacity());
    }

    @Test
    @DisplayName("Datos positivos son válidos")
    void hasValidData_true() {
        // R4: monthlySalary > 0, requestedAmount > 0 cumplidos
        LoanValidation loan = LoanValidation.builder()
                .monthlySalary(2500.0)
                .requestedAmount(10000.0)
                .termMonths(12)
                .build();

        assertTrue(loan.hasValidData());
    }

    @Test
    @DisplayName("Datos negativos son inválidos")
    void hasValidData_false() {
        // R4: sueldo negativo falla la validacion basica
        LoanValidation loan = LoanValidation.builder()
                .monthlySalary(-100.0)
                .requestedAmount(10000.0)
                .termMonths(12)
                .build();

        assertFalse(loan.hasValidData());
    }
}