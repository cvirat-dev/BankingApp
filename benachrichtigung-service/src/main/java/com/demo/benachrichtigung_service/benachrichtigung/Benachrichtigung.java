package com.demo.benachrichtigung_service.benachrichtigung;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity @Data
public class Benachrichtigung {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String nachricht;
    private LocalDateTime timestamp;
}
