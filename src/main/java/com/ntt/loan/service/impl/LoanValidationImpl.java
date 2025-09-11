package com.ntt.loan.service.impl;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.dto.LoanValidationResult;
import com.ntt.loan.mapper.LoanValidationMapper;
import com.ntt.loan.model.LoanValidation;
import com.ntt.loan.model.LoanValidationReason;
import com.ntt.loan.service.LoanValidationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanValidationImpl implements LoanValidationService {

    private final LoanValidationMapper mapper;
    private final Clock clock;

    public LoanValidationImpl(LoanValidationMapper mapper, Clock clock) {
        this.mapper = mapper;
        this.clock = clock;
    }

    @Override
    public Mono<LoanValidationResult> validateLoan(LoanValidationRequest request) {
        try {
            LoanValidationResult result = performValidation(request);
            return Mono.just(result);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
    
    private LoanValidationResult performValidation(LoanValidationRequest request) {
        // crear el modelo para trabajar
        LoanValidation loanValidation = mapper.toModel(request);
        
        // calcular cuanto va a pagar por mes (division simple - @Valid garantiza no nulls)
        double monthlyPayment = request.requestedAmount() / request.termMonths();
        loanValidation.setMonthlyPayment(monthlyPayment);

        // aplicar reglas de negocio una por una
        List<LoanValidationReason> reasons = new ArrayList<>();
        
        validateRecentLoans(request).ifPresent(reasons::add);
        validateTermLimits(request).ifPresent(reasons::add);
        validatePaymentCapacity(request, monthlyPayment).ifPresent(reasons::add);
        validateDataIntegrity(request).ifPresent(reasons::add);

        // determinar elegibilidad basado en si hay razones de rechazo
        boolean eligible = reasons.isEmpty();

        // setear el resultado final y convertir a DTO
        loanValidation.setEligible(eligible);
        loanValidation.setReasons(reasons);

        return mapper.toDto(loanValidation);
    }

    // R1 - validacion de prestamos recientes - > since = today.minusMonths(3) con Clock inyectable para tests.}
    // El solicitante no debe tener préstamos en los últimos 3 meses (incluyente).
    private Optional<LoanValidationReason> validateRecentLoans(LoanValidationRequest request) {
        return Optional.ofNullable(request.lastLoanDate())
                .filter(date -> !date.isBefore(LocalDate.now(clock).minusMonths(3)))
                .map(date -> LoanValidationReason.HAS_RECENT_LOANS);
    }

    // R2 - validacion de limites de plazo -> termMonths ≤ 36 y termMonths ≥ 1.
    private Optional<LoanValidationReason> validateTermLimits(LoanValidationRequest request) {
        if (request.termMonths() < 1 || request.termMonths() > 36) {
            return Optional.of(LoanValidationReason.PLAZO_MAXIMO_SUPERADO);
        }
        return Optional.empty();
    }

    // R3 - validacion de capacidad de pago - > debe cumplir monthlyPayment ≤ 0.40 * monthlySalary.
    private Optional<LoanValidationReason> validatePaymentCapacity(LoanValidationRequest request, double monthlyPayment) {
        if (monthlyPayment > request.monthlySalary() * 0.40) {
            return Optional.of(LoanValidationReason.CAPACIDAD_INSUFICIENTE);
        }
        return Optional.empty();
    }

    // R4 - validacion de integridad de datos - >  monthlySalary > 0, requestedAmount > 0.
    private Optional<LoanValidationReason> validateDataIntegrity(LoanValidationRequest request) {
        if (request.monthlySalary() <= 0 || request.requestedAmount() <= 0) {
            return Optional.of(LoanValidationReason.DATOS_INVALIDOS);
        }
        return Optional.empty();
    }
}
