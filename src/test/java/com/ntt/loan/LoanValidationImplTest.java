package com.ntt.loan;

import com.ntt.loan.dto.LoanValidationRequest;
import com.ntt.loan.mapper.LoanValidationMapper;
import com.ntt.loan.model.LoanValidationReason;
import com.ntt.loan.service.impl.LoanValidationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanValidationImplTest {

    private LoanValidationImpl service;

    @Mock
    private Clock clock;

    private LocalDate fixedDate;
    private Instant fixedInstant;

    @BeforeEach
    void setUp() {
        reset(clock);
        LoanValidationMapper mapper = new LoanValidationMapper();
        service = new LoanValidationImpl(mapper, clock);
        fixedDate = LocalDate.of(2025, 9, 11);
        fixedInstant = fixedDate.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    @Test
    @DisplayName("Debe aprobar el préstamo cuando todas las reglas de validación pasan")
    void eligibleWhenValid() {
        // caso feliz
        LoanValidationRequest request = new LoanValidationRequest(2500.0, 6000.0, 24, null);

        StepVerifier.create(service.validateLoan(request))
                .assertNext(result -> {
                    assert result.eligible(); // debe ser elegible
                    assert result.reasons().isEmpty(); // sin razones de rechazo
                    assert result.monthlyPayment() == 250.0; // pago correcto
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe rechazar el préstamo cuando el cliente tiene préstamo reciente dentro de 3 meses")
    void notEligibleWhenRecentLoan() {
        // prestamo muy reciente - debe fallar R1
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        LocalDate recent = fixedDate.minusMonths(2); // hace 2 meses
        LoanValidationRequest request = new LoanValidationRequest(2500.0, 6000.0, 24, recent);

        StepVerifier.create(service.validateLoan(request))
                .assertNext(result -> {
                    assert !result.eligible(); // no elegible
                    assert result.reasons().contains(LoanValidationReason.HAS_RECENT_LOANS.name()); // razon correcta
                    assert result.monthlyPayment() == null; // no payment
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe rechazar el préstamo cuando el plazo supera el máximo de 36 meses")
    void notEligibleWhenTermTooLong() {
        // plazo muy largo - mas de 36 meses
        LoanValidationRequest request = new LoanValidationRequest(2500.0, 6000.0, 37, null);

        StepVerifier.create(service.validateLoan(request))
                .assertNext(result -> {
                    assert !result.eligible(); // rechazado
                    assert result.reasons().contains(LoanValidationReason.PLAZO_MAXIMO_SUPERADO.name()); // razon de plazo
                    assert result.monthlyPayment() == null; // sin pago
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe rechazar el préstamo cuando el pago mensual supera el 40% del salario")
    void notEligibleWhenInsufficientCapacity() {
        // salario muy bajo para el prestamo - falla la regla de 40%
        LoanValidationRequest request = new LoanValidationRequest(1000.0, 12000.0, 20, null);

        StepVerifier.create(service.validateLoan(request))
                .assertNext(result -> {
                    assert !result.eligible(); // no puede pagar
                    assert result.reasons().contains(LoanValidationReason.CAPACIDAD_INSUFICIENTE.name()); // falta capacidad
                    assert result.monthlyPayment() == null; // no payment
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe rechazar el préstamo cuando los datos de entrada son inválidos")
    void notEligibleWhenInvalidData() {
        // datos invalidos - salario negativo
        LoanValidationRequest request = new LoanValidationRequest(-100.0, 6000.0, 24, null);

        StepVerifier.create(service.validateLoan(request))
                .assertNext(result -> {
                    assert !result.eligible(); // datos malos
                    assert result.reasons().contains(LoanValidationReason.DATOS_INVALIDOS.name()); // razon de invalidez
                    assert result.monthlyPayment() == null; // no payment
                })
                .verifyComplete();
    }
}
