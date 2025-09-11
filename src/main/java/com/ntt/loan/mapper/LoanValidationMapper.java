package com.ntt.loan.mapper;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.dto.LoanValidationResult;
import com.ntt.loan.model.LoanValidation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LoanValidationMapper {

    // convierte el request DTO al modelo interno
    public LoanValidation toModel(LoanValidationRequest request) {
        Double monthlyPayment = request.termMonths() > 0 ?
                request.requestedAmount() / request.termMonths()
                : 0.0;
            
        return LoanValidation.builder()
                .monthlySalary(request.monthlySalary())
                .requestedAmount(request.requestedAmount())
                .termMonths(request.termMonths())
                .lastLoanDate(request.lastLoanDate())
                .monthlyPayment(monthlyPayment)
                .build();
    }

    // convierte el modelo interno de vuelta al DTO , uso de programacuoin funcional
    public LoanValidationResult toDto(LoanValidation model) {
        // usar programacion funcional para mapear los reasons
        List<String> reasonStrings = Optional.ofNullable(model.getReasons())
            .map(reasons -> reasons.stream() // ← STREAM  aquí
                .map(Enum::name)   // ← LAMBDA  aquí
                .collect(Collectors.toList()))
            .orElse(Collections.emptyList());
        
        return new LoanValidationResult(
            model.isEligible(),
            reasonStrings,
            model.getMonthlyPayment()
        );
    }
}