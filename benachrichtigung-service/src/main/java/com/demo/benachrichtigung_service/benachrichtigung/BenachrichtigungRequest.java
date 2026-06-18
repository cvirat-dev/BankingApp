package com.demo.benachrichtigung_service.benachrichtigung;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BenachrichtigungRequest {

    @NotNull(message = "Typ darf nicht leer sein!")
    private BenachrichtigungTyp typ;

    @NotNull(message = "KontoId darf nicht leer sein!")
    private Long kontoId;

    @NotBlank(message = "IBAN darf nicht leer sein!")
    @Size(max = 22, message = "IBAN darf maximal 22 Zeichen lang sein!")
    private String iban;

    @NotBlank(message = "Inhaber darf nicht leer sein!")
    private String inhaber;

    @NotBlank(message = "Nachricht darf nicht leer sein!")
    @Size(max = 200, message = "Nachricht darf maximal 200 Zeichen lang sein!")
    private String nachricht;
}
