package com.demo.kontoservice.konto;


import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KontoCreateRequest {

    @NotNull(message = "Inhaber darf nicht null sein")
    private String inhaber;

    @NotNull(message = "Kontostand darf nicht null sein")
    private BigDecimal kontostand;
}
