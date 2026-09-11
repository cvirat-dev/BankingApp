package com.demo.kontoservice.konto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KontoUpdateRequest {

    @NotNull(message = "ID darf nicht null sein")
    private Long id;

    @NotNull(message = "Inhaber darf nicht null sein")
    private String inhaber;
}
