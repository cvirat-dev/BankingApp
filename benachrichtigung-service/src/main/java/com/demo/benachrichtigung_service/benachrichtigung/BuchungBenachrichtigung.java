package com.demo.benachrichtigung_service.benachrichtigung;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("BUCHUNG")
@Getter @Setter @NoArgsConstructor
public class BuchungBenachrichtigung extends Benachrichtigung {
    private Long buchungId;
    private Long kontoId;
    private String iban;
    private String inhaber;
    private Double betrag;
}
