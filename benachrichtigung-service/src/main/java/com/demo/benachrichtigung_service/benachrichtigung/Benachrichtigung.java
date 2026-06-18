package com.demo.benachrichtigung_service.benachrichtigung;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity @Data
public class Benachrichtigung {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private BenachrichtigungTyp typ;
    private Long kontoId;
    private String iban;
    private String inhaber;
    private String nachricht;
    private LocalDateTime timestamp;
}
