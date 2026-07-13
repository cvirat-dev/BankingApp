package com.demo.kontoservice.transaktion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Transaktion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quelleKontoId;
    private Long zielKontoId;
    private BigDecimal betrag;
    private String beschreibung;
    private LocalDateTime timestamp;
}
