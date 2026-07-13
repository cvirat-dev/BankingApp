package com.demo.kontoservice.benachrichtigung;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class TransaktionBenachrichtigungRequest extends BenachrichtigungBase {
    private final BenachrichtigungTyp typ = BenachrichtigungTyp.TRANSAKTION;
    private Long transaktionId;
    private Long quelleKontoId;
    private Long zielKontoId;
    private String quelleIban;
    private String zielIban;
    private String quelleInhaber;
    private String zielInhaber;
    private BigDecimal betrag;
}
