package com.demo.kontoservice.benachrichtigung;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor
@ToString(callSuper = true)
public class KontoBenachrichtigungRequest extends BenachrichtigungBase {
    private final BenachrichtigungTyp typ = BenachrichtigungTyp.KONTO;
    private Aktion aktion;
    private String iban;
    private String inhaber;
    private Long kontoId;
}
