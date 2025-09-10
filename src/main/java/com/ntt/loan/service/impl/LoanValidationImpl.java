package com.ntt.loan.service.impl;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.dto.LoanValidationResult;
import com.ntt.loan.mapper.LoanValidationMapper;
import com.ntt.loan.model.LoanValidation;
import com.ntt.loan.model.LoanValidationReason;
import com.ntt.loan.service.LoanValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanValidationImpl implements LoanValidationService {

    private final Clock clock;

    private final LoanValidationMapper mapper;

    public LoanValidationImpl(Clock clock, LoanValidationMapper mapper) {
        this.clock = clock;
        this.mapper = mapper;
    }

    @Override
    public Mono<LoanValidationResult> validateLoan(LoanValidationRequest request) {

        return Mono.fromCallable(() -> {
            LoanValidation loanValidation = mapper.toModel(request);
            List<LoanValidationReason> reasons = new ArrayList<>();
            boolean eligible = true;

            // calcular pago mensual basico
            double monthlyPayment = request.getRequestedAmount() / request.getTermMonths();
            loanValidation.setMonthlyPayment(monthlyPayment);

            // R1 - revisar si tiene prestamos recientes wn ultimos 3 meses
            if (request.getLastLoanDate() != null &&
                    !request.getLastLoanDate().isBefore(LocalDate.now(clock).minusMonths(3))) {
                eligible = false;
                reasons.add(LoanValidationReason.HAS_RECENT_LOANS);
            }

            // R2 - validar que el plazo este entre 1 y 36 meses
            if (request.getTermMonths() < 1 || request.getTermMonths() > 36) {
                eligible = false;
                reasons.add(LoanValidationReason.PLAZO_MAXIMO_SUPERADO);
            }

            // R3 - capacidad de pago no debe exceder 40% del salario
            if (request.getRequestedAmount() / request.getTermMonths() > request.getMonthlySalary() * 0.40) {
                eligible = false;
                reasons.add(LoanValidationReason.CAPACIDAD_INSUFICIENTE);
            }

            // R4 - verificar que los datos sean validos
            if (request.getMonthlySalary() <= 0 || request.getRequestedAmount() <= 0) {
                eligible = false;
                reasons.add(LoanValidationReason.DATOS_INVALIDOS);
            }

            // guardar resultado final
            loanValidation.setEligible(eligible);
            loanValidation.setReasons(reasons);

            return mapper.toDto(loanValidation); //convertir
        });
    }
}
