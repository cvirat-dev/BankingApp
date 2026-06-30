package com.demo.kontoservice.transaktion;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransaktionRequest {

    @NotNull(message = "Quellkonto darf nicht null sein.")
    @Min(value = 1, message = "Quellkonto muss eine positive Zahl sein.")
    private Long quelleKontoId;

    @NotNull(message = "Zielkonto darf nicht null sein.")
    @Min(value = 1, message = "Zielkonto muss eine positive Zahl sein.")
    private Long zielKontoId;

    @NotNull(message = "Betrag darf nicht null sein.")
    @DecimalMin(value = "0.01", message = "Betrag muss größer als 0 sein.") 
    private BigDecimal betrag;

    @NotBlank(message = "Beschreibung darf nicht leer sein.")
    private String beschreibung;
}
