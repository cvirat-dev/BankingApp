package com.demo.kontoservice.konto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KontoService {
    @Autowired private KontoRepository kontoRepository;
    @Autowired private TransaktionRepository transaktionRepository;
    @Autowired private RestTemplate restTemplate;

    public Transaktion buchung(Long kontoId, BigDecimal betrag, String beschreibung) {
        // Konto laden
        Konto konto = kontoRepository.findById(kontoId).orElseThrow();
        konto.setKontostand(konto.getKontostand().add(betrag));
        kontoRepository.save(konto);

        Transaktion transaktion = new Transaktion();
        transaktion.setKontoId(kontoId); transaktion.setBetrag(betrag);
        transaktion.setBeschreibung(beschreibung); transaktion.setDatum(LocalDateTime.now());

        // Benachrichtung auslösen (Fire-and-Forget)
        restTemplate.postForObject(
            "http://benachrichtigung-service:8082/api/benachrichtigungen",
            Map.of("nachricht", "Buchung: " + betrag + "EUR auf Konto " + kontoId),
            Void.class
        );
        
        return transaktion;
    }

    public List<Transaktion> getTransaktionen(Long id) {
        return transaktionRepository.findByKontoId(id);
    }
}
