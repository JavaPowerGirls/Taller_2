package com.ntt.loan.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

public record LoanValidationResult(
    boolean eligible,
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // para que no salga si la lista esta vacia
    List<String> reasons,
    @JsonInclude(JsonInclude.Include.NON_NULL) // patra que no salga en la respuesta si es null
    Double monthlyPayment
) {
}
