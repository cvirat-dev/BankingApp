package com.demo.kontoservice.konto;


import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KontoRequest {

    @NotNull(message = "IBAN darf nicht null sein")
    @Size(min = 22, max = 22, message = "IBAN muss genau 22 Zeichen lang sein")
    private String iban;

    @NotNull(message = "Inhaber darf nicht null sein")
    private String inhaber;

    @NotNull(message = "Kontostand darf nicht null sein")
    private BigDecimal kontostand;
}
