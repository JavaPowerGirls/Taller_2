package com.ntt.loan.service.impl;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.dto.LoanValidationResult;
import com.ntt.loan.mapper.LoanValidationMapper;
import com.ntt.loan.model.LoanValidation;
import com.ntt.loan.model.LoanValidationReason;
import com.ntt.loan.service.LoanValidationService;

import reactor.core.publisher.Mono;

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
        return Mono.fromSupplier(() -> {
            return performValidation(request);
        });
    }
    
    private LoanValidationResult performValidation(LoanValidationRequest request) {
        // crear el modelo usando builder con los datos del request
        LoanValidation loanValidation =  mapper.toModel(request);

        // aplicar reglas de negocio usando funciones del modelo
        List<LoanValidationReason> reasons = new ArrayList<>();

        if (loanValidation.hasRecentLoans(clock)) {
            reasons.add(LoanValidationReason.HAS_RECENT_LOANS);
        }

        if (!loanValidation.isValidTerm()) {
            reasons.add(LoanValidationReason.PLAZO_MAXIMO_SUPERADO);
        }

        if (!loanValidation.hasPaymentCapacity()) {
            reasons.add(LoanValidationReason.CAPACIDAD_INSUFICIENTE);
        }

        if (!loanValidation.hasValidData()) {
            reasons.add(LoanValidationReason.DATOS_INVALIDOS);
        }

        // determinar elegibilidad basado en si hay razones de rechazo
        boolean eligible = reasons.isEmpty();

        // setear el resultado final
        loanValidation.setEligible(eligible);
        loanValidation.setReasons(reasons);

        return mapper.toDto(loanValidation);
    }

}
