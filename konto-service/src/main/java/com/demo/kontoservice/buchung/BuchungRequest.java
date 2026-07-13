package com.demo.kontoservice.buchung;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BuchungRequest {

    @NotNull(message = "Konto ID darf nicht null sein")
    @Min(value = 1, message = "Konto ID muss größer als 0 sein")
    private Long kontoId;

    @NotNull(message = "Betrag darf nicht null sein")
    private BigDecimal betrag;

    @NotBlank(message = "Beschreibung darf nicht leer sein")
    private String beschreibung;
}
