package com.demo.kontoservice.benachrichtigung;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class KontoBenachrichtigungRequest extends BenachrichtigungBase {
    private final BenachrichtigungTyp typ = BenachrichtigungTyp.KONTO;
    private Long kontoId;
    private String iban;
    private String inhaber;
}
