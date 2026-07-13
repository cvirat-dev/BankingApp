package com.demo.kontoservice.buchung;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Buchung {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Long kontoId; // Referenz auf Konto
    private BigDecimal betrag; // Positiv für Einzahlung, Negativ für Auszahlung
    private String beschreibung; // Optional: Beschreibung der Transaktion
    private LocalDateTime datum; // Zeitstempel der Transaktion
}