package com.demo.benachrichtigung_service.benachrichtigung;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("TRANSAKTION")
@Getter @Setter @NoArgsConstructor
public class TransaktionBenachrichtigung extends Benachrichtigung {
    private Long transaktionId;
    private Long quelleKontoId;
    private Long zielKontoId;
    private String quelleIban;
    private String zielIban;
    private String quelleInhaber;
    private String zielInhaber;
    private Double betrag;
}
