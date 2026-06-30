package com.demo.kontoservice.benachrichtigung;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class BenachrichtigungBase {
    private String nachricht;
    private LocalDateTime timestamp;
}
