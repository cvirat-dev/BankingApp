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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KontoService {
    @Autowired private KontoRepository kontoRepository;
    @Autowired private KontoDbService kontoDbService;
    @Autowired private TransaktionRepository transaktionRepository;
    @Autowired private RestTemplate restTemplate;

    public Konto createKonto(Konto konto) {
        log.info("Starte Kontoerstellung fuer Inhaber={}", konto.getInhaber());
        log.debug("Eingehendes Konto fuer Erstellung: {}", konto);

        Konto gespeichertesKonto = kontoDbService.erstelleKontoInDb(konto);
        log.info(
            "Konto erfolgreich erstellt: kontoId={}, iban={}, inhaber={}",
            gespeichertesKonto.getId(),
            gespeichertesKonto.getIban(),
            gespeichertesKonto.getInhaber()
        );
        log.debug("Persistiertes Kontoobjekt: {}", gespeichertesKonto);

        // FAT-Event (Microservices konform)
        BenachrichtigungRequest request = new BenachrichtigungRequest(
            BenachrichtigungTyp.KONTO,
            gespeichertesKonto.getId(),
            gespeichertesKonto.getIban(),
            gespeichertesKonto.getInhaber(),
            "Neues Konto erstellt: " + konto.getInhaber()
        );
        log.info("Sende Konto-Benachrichtigung fuer kontoId={}", gespeichertesKonto.getId());
        log.debug("Konto-Benachrichtigung Request: {}", request);
        restTemplate.postForObject(
                "http://benachrichtigung-service:8082/api/benachrichtigungen",
            request,
                Void.class
        );
        log.info("Konto-Benachrichtigung erfolgreich versendet fuer kontoId={}", gespeichertesKonto.getId());
        
        return gespeichertesKonto;
    }

    @Transactional
    public Transaktion buchung(Long kontoId, BigDecimal betrag, String beschreibung) {
        log.info("Starte Buchung fuer kontoId={} mit betrag={}", kontoId, betrag);
        log.debug("Buchungsdetails: beschreibung={}", beschreibung);

        Konto konto = kontoRepository.findById(kontoId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Konto mit ID " + kontoId + " nicht gefunden")
        );
        BigDecimal alterKontostand = konto.getKontostand();
        BigDecimal neuerKontostand = alterKontostand.add(betrag);
        konto.setKontostand(neuerKontostand);
        kontoRepository.save(konto);
        log.info(
            "Buchung auf Konto angewendet: kontoId={}, alterKontostand={}, neuerKontostand={}",
            kontoId,
            alterKontostand,
            neuerKontostand
        );
        log.debug("Aktualisiertes Konto nach Buchung: {}", konto);

        Transaktion transaktion = new Transaktion();
        transaktion.setKontoId(kontoId);
        transaktion.setBetrag(betrag);
        transaktion.setBeschreibung(beschreibung);
        transaktion.setDatum(LocalDateTime.now());
        transaktionRepository.save(transaktion);
        log.info("Transaktion gespeichert fuer kontoId={} mit transaktionId={}", kontoId, transaktion.getId());
        log.debug("Persistierte Transaktion: {}", transaktion);

        String sign = betrag.signum() >= 0 ? "+" : "";
        BenachrichtigungRequest request = new BenachrichtigungRequest(
                BenachrichtigungTyp.TRANSAKTION,
                konto.getId(),
                konto.getIban(),
                konto.getInhaber(),
                "Buchung: " + sign + betrag + " €"
        );

        log.info("Sende Transaktions-Benachrichtigung fuer kontoId={}", kontoId);
        log.debug("Transaktions-Benachrichtigung Request: {}", request);
        restTemplate.postForObject(
                "http://benachrichtigung-service:8082/api/benachrichtigungen",
                request,
                Void.class
        );
        log.info("Transaktions-Benachrichtigung erfolgreich versendet fuer kontoId={}", kontoId);
        
        return transaktion;
    }

    public List<Transaktion> getTransaktionen(Long id) {
        log.debug("Lade Transaktionen fuer kontoId={}", id);
        List<Transaktion> transaktionen = transaktionRepository.findByKontoId(id);
        log.info("Transaktionen geladen fuer kontoId={}: anzahl={}", id, transaktionen.size());
        return transaktionen;
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Starte Loeschen von kontoId={}", id);

        kontoRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Konto nicht gefunden"));
        log.debug("Konto fuer Loeschung gefunden: kontoId={}", id);

        transaktionRepository.deleteByKontoId(id);
        log.info("Zugehoerige Transaktionen geloescht fuer kontoId={}", id);
        kontoRepository.deleteById(id);
        log.info("Konto erfolgreich geloescht: kontoId={}", id);
    }
}
