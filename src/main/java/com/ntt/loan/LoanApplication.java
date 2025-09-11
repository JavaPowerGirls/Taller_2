package com.ntt.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class LoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanApplication.class, args);
    }
    // Configuracion para inyectar Clock en servicios para obtener tiempo actual
    @Bean // crea un objeto que Spring debe manejar
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
