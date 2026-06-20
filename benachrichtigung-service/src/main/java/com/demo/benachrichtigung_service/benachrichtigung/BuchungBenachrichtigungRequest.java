package com.demo.benachrichtigung_service.benachrichtigung;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BuchungBenachrichtigungRequest extends BenachrichtigungRequest {

    @NotNull(message = "BuchungId darf nicht leer sein!")
    private Long buchungId;

    @NotNull(message = "KontoId darf nicht leer sein!")
    private Long kontoId;

    @NotBlank(message = "IBAN darf nicht leer sein!")
    @Size(max = 22, message = "IBAN darf maximal 22 Zeichen lang sein!")
    private String iban;

    @NotBlank(message = "Inhaber darf nicht leer sein!")
    private String inhaber;

    @NotNull(message = "Betrag darf nicht leer sein!")
    private Double betrag;
}