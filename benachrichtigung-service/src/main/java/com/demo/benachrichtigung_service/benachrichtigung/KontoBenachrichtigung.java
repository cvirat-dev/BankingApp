package com.demo.benachrichtigung_service.benachrichtigung;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("KONTO")
@Getter @Setter @NoArgsConstructor
public class KontoBenachrichtigung extends Benachrichtigung {
    private Long kontoId;
    private String iban;
    private String inhaber;
    private AktionTyp aktion;
}
