package com.demo.benachrichtigung_service.benachrichtigung;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TransaktionBenachrichtigungRequest extends BenachrichtigungRequest {

    public TransaktionBenachrichtigungRequest() {
        super(BenachrichtigungTyp.TRANSAKTION);
    }

    @NotNull(message = "TransaktionId darf nicht leer sein!")
    private Long transaktionId;

    @NotNull(message = "QuelleKontoId darf nicht leer sein!")
    private Long quelleKontoId;

    @NotNull(message = "ZielKontoId darf nicht leer sein!")
    private Long zielKontoId;

    @NotBlank(message = "Quelle IBAN darf nicht leer sein!")
    @Size(max = 22, message = "Quelle IBAN darf maximal 22 Zeichen lang sein!")
    private String quelleIban;

    @NotBlank(message = "Ziel IBAN darf nicht leer sein!")
    @Size(max = 22, message = "Ziel IBAN darf maximal 22 Zeichen lang sein!")
    private String zielIban;

    @NotBlank(message = "Quelle Inhaber darf nicht leer sein!")
    private String quelleInhaber;

    @NotBlank(message = "Ziel Inhaber darf nicht leer sein!")
    private String zielInhaber;

    @NotNull(message = "Betrag darf nicht leer sein!")
    private Double betrag;
}