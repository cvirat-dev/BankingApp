package com.demo.benachrichtigung_service.benachrichtigung;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class KontoBenachrichtigungRequest extends BenachrichtigungRequest {

    public KontoBenachrichtigungRequest() {
        super(BenachrichtigungTyp.KONTO);
    }

    @NotNull(message = "KontoId darf nicht leer sein!")
    private Long kontoId;

    @NotBlank(message = "IBAN darf nicht leer sein!")
    @Size(max = 22, message = "IBAN darf maximal 22 Zeichen lang sein!")
    private String iban;

    @NotBlank(message = "Inhaber darf nicht leer sein!")
    private String inhaber;

    @NotNull(message = "Aktion darf nicht leer sein!")
    private AktionTyp aktion;
}