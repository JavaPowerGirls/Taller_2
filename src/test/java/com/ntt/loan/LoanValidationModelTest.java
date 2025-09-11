package com.ntt.loan;

import com.ntt.loan.model.LoanValidation;
import org.junit.jupiter.api.BeforeEach;
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
    void hasRecentLoans_WithinThreeMonths_ShouldReturnTrue() {
        // Arrange
        LocalDate recentLoanDate = fixedDate.minusMonths(2);
        LoanValidation loan = LoanValidation.builder()
            .lastLoanDate(recentLoanDate)
            .build();

        // Act
        boolean result = loan.hasRecentLoans(fixedClock);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasRecentLoans_MoreThanThreeMonths_ShouldReturnFalse() {
        // Arrange
        LocalDate oldLoanDate = fixedDate.minusMonths(3).minusDays(1);
        LoanValidation loan = LoanValidation.builder()
            .lastLoanDate(oldLoanDate)
            .build();

        // Act
        boolean result = loan.hasRecentLoans(fixedClock);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidTerm_ValidRange_ShouldReturnTrue() {
        // Arrange
        LoanValidation loan = LoanValidation.builder()
            .termMonths(24)
            .build();

        // Act
        boolean result = loan.isValidTerm();

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidTerm_InvalidRange_ShouldReturnFalse() {
        // Arrange
        LoanValidation loan = LoanValidation.builder()
            .termMonths(37)
            .build();

        // Act
        boolean result = loan.isValidTerm();

        // Assert
        assertFalse(result);
    }

    @Test
    void hasPaymentCapacity_UnderFortyPercent_ShouldReturnTrue() {
        // Arrange
        LoanValidation loan = LoanValidation.builder()
            .monthlyPayment(300.0)  // 30% of 1000
            .monthlySalary(1000.0)
            .build();

        // Act
        boolean result = loan.hasPaymentCapacity();

        // Assert
        assertTrue(result);
    }

    @Test
    void hasPaymentCapacity_OverFortyPercent_ShouldReturnFalse() {
        // Arrange
        LoanValidation loan = LoanValidation.builder()
            .monthlyPayment(500.0)  // 50% of 1000
            .monthlySalary(1000.0)
            .build();

        // Act
        boolean result = loan.hasPaymentCapacity();

        // Assert
        assertFalse(result);
    }

    @Test
    void hasValidData_ValidData_ShouldReturnTrue() {
        // Arrange
        LoanValidation loan = LoanValidation.builder()
            .monthlySalary(2500.0)
            .requestedAmount(10000.0)
            .termMonths(12)
            .build();

        // Act
        boolean result = loan.hasValidData();

        // Assert
        assertTrue(result);
    }

    @Test
    void hasValidData_InvalidData_ShouldReturnFalse() {
        // Arrange
        LoanValidation loan = LoanValidation.builder()
            .monthlySalary(-100.0)  // Invalid: negative
            .requestedAmount(10000.0)
            .termMonths(12)
            .build();

        // Act
        boolean result = loan.hasValidData();

        // Assert
        assertFalse(result);
    }
}