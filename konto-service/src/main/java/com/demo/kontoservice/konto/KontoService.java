package com.demo.kontoservice.konto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.demo.kontoservice.benachrichtigung.BenachrichtigungRequest;
import com.demo.kontoservice.benachrichtigung.BenachrichtigungTyp;

@Service
public class KontoService {
    @Autowired private KontoRepository kontoRepository;
    @Autowired private KontoDbService kontoDbService;
    @Autowired private TransaktionRepository transaktionRepository;
    @Autowired private RestTemplate restTemplate;

    public Konto createKonto(Konto konto) {
        
        Konto gespeichertesKonto = kontoDbService.erstelleKontoInDb(konto);

        // FAT-Event (Microservices konform)
        restTemplate.postForObject(
                "http://benachrichtigung-service:8082/api/benachrichtigungen",
                new BenachrichtigungRequest(
                    BenachrichtigungTyp.KONTO,
                    gespeichertesKonto.getId(),
                    gespeichertesKonto.getIban(),
                    gespeichertesKonto.getInhaber(),
                    "Neues Konto erstellt: " + konto.getInhaber()),
                Void.class
        );
        
        return gespeichertesKonto;
    }

    @Transactional
    public Transaktion buchung(Long kontoId, BigDecimal betrag, String beschreibung) {

        Konto konto = kontoRepository.findById(kontoId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Konto mit ID " + kontoId + " nicht gefunden")
        );
        konto.setKontostand(konto.getKontostand().add(betrag));
        kontoRepository.save(konto);

        Transaktion transaktion = new Transaktion();
        transaktion.setKontoId(kontoId); transaktion.setBetrag(betrag);
        transaktion.setBeschreibung(beschreibung); transaktion.setDatum(LocalDateTime.now());
        transaktionRepository.save(transaktion);

        String sign = betrag.signum() >= 0 ? "+" : "";
        BenachrichtigungRequest request = new BenachrichtigungRequest(
                BenachrichtigungTyp.TRANSAKTION,
                konto.getId(),
                konto.getIban(),
                konto.getInhaber(),
                "Buchung: " + sign + betrag + " €"
        );

        restTemplate.postForObject(
                "http://benachrichtigung-service:8082/api/benachrichtigungen",
                request,
                Void.class
        );
        
        return transaktion;
    }

    public List<Transaktion> getTransaktionen(Long id) {
        return transaktionRepository.findByKontoId(id);
    }

    @Transactional
    public void deleteById(Long id) {

        kontoRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Konto nicht gefunden"));

        transaktionRepository.deleteByKontoId(id);
        kontoRepository.deleteById(id);
    }
}
