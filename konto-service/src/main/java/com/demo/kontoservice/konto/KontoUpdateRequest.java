package com.demo.kontoservice.konto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KontoUpdateRequest {

    @NotNull(message = "ID darf nicht null sein")
    private Long id;

    @NotNull(message = "Inhaber darf nicht null sein")
    private String inhaber;

    @NotNull(message = "IBAN darf nicht null sein")
    private String iban;

    @NotNull(message = "Kontostand darf nicht null sein")
    private BigDecimal kontostand;

}
