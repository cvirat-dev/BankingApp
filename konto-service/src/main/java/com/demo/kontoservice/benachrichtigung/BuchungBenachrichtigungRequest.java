package com.demo.kontoservice.benachrichtigung;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class BuchungBenachrichtigungRequest extends BenachrichtigungBase {
    private final BenachrichtigungTyp typ = BenachrichtigungTyp.BUCHUNG;
    private Long buchungId;
    private Long kontoId;
    private String iban;
    private String inhaber;
    private BigDecimal betrag;
}