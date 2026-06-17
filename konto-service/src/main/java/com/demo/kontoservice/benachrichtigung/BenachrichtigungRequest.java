package com.demo.kontoservice.benachrichtigung;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BenachrichtigungRequest {
    private BenachrichtigungTyp typ;
    private Long kontoId;
    private String iban;
    private String inhaber;
    private String nachricht;
}
