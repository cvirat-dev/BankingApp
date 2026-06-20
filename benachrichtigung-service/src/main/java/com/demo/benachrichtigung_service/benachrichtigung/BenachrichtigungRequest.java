package com.demo.benachrichtigung_service.benachrichtigung;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "typ",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = KontoBenachrichtigungRequest.class, name = "KONTO"),
    @JsonSubTypes.Type(value = BuchungBenachrichtigungRequest.class, name = "BUCHUNG"),
    @JsonSubTypes.Type(value = TransaktionBenachrichtigungRequest.class, name = "TRANSAKTION")
})
public abstract class BenachrichtigungRequest {

    @NotNull(message = "Typ darf nicht leer sein!")
    private BenachrichtigungTyp typ;

    @NotBlank(message = "Nachricht darf nicht leer sein!")
    @Size(max = 200, message = "Nachricht darf maximal 200 Zeichen lang sein!")
    private String nachricht;
}
