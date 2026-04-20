package com.demo.benachrichtigung_service.benachrichtigung;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BenachrichtigungRequest {

    @NotBlank(message = "Nachricht darf nicht leer sein!")
    @Size(max = 200, message = "Nachricht darf maximal 200 Zeichen lang sein!")
    private String nachricht;
}
