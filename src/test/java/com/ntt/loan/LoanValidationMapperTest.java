package com.ntt.loan;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.dto.LoanValidationResult;
import com.ntt.loan.mapper.LoanValidationMapper;
import com.ntt.loan.model.LoanValidation;
import com.ntt.loan.model.LoanValidationReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoanValidationMapperTest {

    private LoanValidationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LoanValidationMapper();
    }

    @Test
    void toModel_WithValidRequest_ShouldMapCorrectly() {
        // Arrange
        LocalDate lastLoanDate = LocalDate.of(2024, 6, 15);
        LoanValidationRequest request = new LoanValidationRequest(
                2500.0,
                10000.0,
                24,
                lastLoanDate
        );

        // Act
        LoanValidation result = mapper.toModel(request);

        // Assert
        assertNotNull(result);
        assertEquals(2500.0, result.getMonthlySalary());
        assertEquals(10000.0, result.getRequestedAmount());
        assertEquals(24, result.getTermMonths());
        assertEquals(lastLoanDate, result.getLastLoanDate());
        assertEquals(416.67, result.getMonthlyPayment(), 0.01); // 10000 / 24
        assertFalse(result.isEligible()); // Default value
    }

    @Test
    void toModel_WithNullLastLoanDate_ShouldMapCorrectly() {
        // Arrange
        LoanValidationRequest request = new LoanValidationRequest(
                3000.0,
                15000.0,
                36,
                null
        );

        // Act
        LoanValidation result = mapper.toModel(request);

        // Assert
        assertNotNull(result);
        assertEquals(3000.0, result.getMonthlySalary());
        assertEquals(15000.0, result.getRequestedAmount());
        assertEquals(36, result.getTermMonths());
        assertNull(result.getLastLoanDate());
        assertEquals(416.67, result.getMonthlyPayment(), 0.01); // 15000 / 36
    }

    @Test
    void toDto_WithEligibleLoan_ShouldMapCorrectly() {
        // Arrange
        LoanValidation model = LoanValidation.builder()
                .monthlySalary(2500.0)
                .requestedAmount(10000.0)
                .termMonths(24)
                .monthlyPayment(416.67)
                .lastLoanDate(null)
                .eligible(true)
                .reasons(new ArrayList<>())
                .build();

        // Act
        LoanValidationResult result = mapper.toDto(model);

        // Assert
        assertNotNull(result);
        assertTrue(result.eligible());
        assertNotNull(result.reasons());
        assertTrue(result.reasons().isEmpty());
        assertEquals(416.67, result.monthlyPayment());
    }

    @Test
    void toDto_WithNotEligibleLoan_ShouldMapCorrectly() {
        // Arrange
        List<LoanValidationReason> reasons = Arrays.asList(
                LoanValidationReason.HAS_RECENT_LOANS,
                LoanValidationReason.CAPACIDAD_INSUFICIENTE
        );
        
        LoanValidation model = LoanValidation.builder()
                .monthlySalary(1000.0)
                .requestedAmount(20000.0)
                .termMonths(12)
                .monthlyPayment(1666.67)
                .lastLoanDate(LocalDate.of(2025, 7, 1))
                .eligible(false)
                .reasons(reasons)
                .build();

        // Act
        LoanValidationResult result = mapper.toDto(model);

        // Assert
        assertNotNull(result);
        assertFalse(result.eligible());
        assertNotNull(result.reasons());
        assertEquals(2, result.reasons().size());
        assertTrue(result.reasons().contains(LoanValidationReason.HAS_RECENT_LOANS.name()));
        assertTrue(result.reasons().contains(LoanValidationReason.CAPACIDAD_INSUFICIENTE.name()));
        assertNull(result.monthlyPayment()); // Should be null for ineligible loans
    }

    @Test
    void toModel_MonthlyPaymentCalculation_ShouldBeAccurate() {
        // Arrange
        LoanValidationRequest request1 = new LoanValidationRequest(2000.0, 12000.0, 12, null);
        LoanValidationRequest request2 = new LoanValidationRequest(3000.0, 24000.0, 24, null);

        // Act
        LoanValidation result1 = mapper.toModel(request1);
        LoanValidation result2 = mapper.toModel(request2);

        // Assert
        assertEquals(1000.0, result1.getMonthlyPayment(), 0.01); // 12000 / 12
        assertEquals(1000.0, result2.getMonthlyPayment(), 0.01); // 24000 / 24
    }

    @Test
    void toModel_WithZeroTermMonths_ShouldHandleGracefully() {
        // Arrange - caso extremo: termMonths = 0
        LoanValidationRequest request = new LoanValidationRequest(
                2500.0,
                10000.0,
                0, // termMonths = 0 -> división por cero
                null
        );

        // Act
        LoanValidation result = mapper.toModel(request);

        // Assert
        assertNotNull(result);
        assertEquals(2500.0, result.getMonthlySalary());
        assertEquals(10000.0, result.getRequestedAmount());
        assertEquals(0, result.getTermMonths());
        assertEquals(0.0, result.getMonthlyPayment()); // Debe ser 0.0 para evitar división por cero
        assertNull(result.getLastLoanDate());
        assertFalse(result.isEligible());
    }
}
