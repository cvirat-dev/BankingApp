package com.demo.benachrichtigung_service.benachrichtigung;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BenachrichtigungEvent {
    private Long kontoId;
    private String inhaber;
    private String nachricht;
    private LocalDateTime timestamp;
}
