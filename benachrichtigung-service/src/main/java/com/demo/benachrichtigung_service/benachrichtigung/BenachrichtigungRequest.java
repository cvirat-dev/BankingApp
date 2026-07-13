package com.demo.benachrichtigung_service.benachrichtigung;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public abstract class BenachrichtigungRequest {

    @NotNull(message = "Typ darf nicht leer sein!")
    @Setter(AccessLevel.NONE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final BenachrichtigungTyp typ;

    @NotBlank(message = "Nachricht darf nicht leer sein!")
    @Size(max = 200, message = "Nachricht darf maximal 200 Zeichen lang sein!")
    private String nachricht;

    protected BenachrichtigungRequest(BenachrichtigungTyp typ) {
        this.typ = typ;
    }
}
