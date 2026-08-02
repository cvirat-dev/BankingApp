package com.demo.kontoservice.buchung;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.demo.kontoservice.benachrichtigung.Aktion;
import com.demo.kontoservice.benachrichtigung.KontoBenachrichtigungRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.demo.kontoservice.CrudService;
import com.demo.kontoservice.benachrichtigung.BuchungBenachrichtigungRequest;
import com.demo.kontoservice.konto.Konto;
import com.demo.kontoservice.konto.KontoService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BuchungService implements CrudService<Buchung, BuchungRequest> {

    @Autowired
    private KontoService kontoService;

    @Autowired
    private BuchungRepository buchungRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Buchung> getBuchungen(Long kontoId) {
        log.debug("Lade Buchungen fuer kontoId={}", kontoId);
        List<Buchung> buchungen = buchungRepository.findByKontoId(kontoId);
        log.info("Buchungen geladen fuer kontoId={}: anzahl={}", kontoId, buchungen.size());
        return buchungen;
    }

    @Override
    public List<Buchung> getAll() {
        log.info("Lade alle Buchungen");
        List<Buchung> buchungen = buchungRepository.findAll();
        log.info("Alle Buchungen geladen: anzahl={}", buchungen.size());
        return buchungen;
    }

    @Override
    public Buchung get(Long id) {
        log.info("Lade Buchung fuer buchungId={}", id);
        Buchung buchung = buchungRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Buchung nicht gefunden"));
        log.debug("Buchung geladen: {}", buchung);
        return buchung;
    }

    @Override
    public Buchung create(BuchungRequest buchungRequest) {
        log.info("Starte Buchung fuer kontoId={} mit betrag={}", buchungRequest.getKontoId(), buchungRequest.getBetrag());
        log.debug("Buchungsdetails: beschreibung={}", buchungRequest.getBeschreibung());

        Buchung buchung = new Buchung();
        buchung.setKontoId(buchungRequest.getKontoId());
        buchung.setBetrag(buchungRequest.getBetrag());
        buchung.setBeschreibung(buchungRequest.getBeschreibung());

        Konto konto = kontoService.get(buchung.getKontoId());
        BigDecimal alterKontostand = konto.getKontostand();
        BigDecimal neuerKontostand = alterKontostand.add(buchung.getBetrag());
        konto.setKontostand(neuerKontostand);
        kontoService.save(konto);
        log.info(
                "Buchung auf Konto angewendet: kontoId={}, alterKontostand={}, neuerKontostand={}",
                buchung.getKontoId(),
                alterKontostand,
                neuerKontostand
        );
        log.debug("Aktualisiertes Konto nach Buchung: {}", konto);

        buchung.setDatum(LocalDateTime.now());
        buchungRepository.save(buchung);
        log.info("Buchung gespeichert fuer kontoId={} mit buchungId={}", buchung.getKontoId(), buchung.getId());
        log.debug("Persistierte Buchung: {}", buchung);

        // FAT-Event (Microservices konform)
        BuchungBenachrichtigungRequest benachrichtigungRequest = new BuchungBenachrichtigungRequest();
        benachrichtigungRequest.setBuchungId(buchung.getId());
        benachrichtigungRequest.setKontoId(konto.getId());
        benachrichtigungRequest.setIban(konto.getIban());
        benachrichtigungRequest.setInhaber(konto.getInhaber());
        benachrichtigungRequest.setBetrag(buchung.getBetrag());
        benachrichtigungRequest.setNachricht(
                String.format(
                        "Neue Buchung erstellt fuer Konto=%s und Betrag=%s",
                        konto.getInhaber(),
                        buchung.getBetrag()
                )
        );
        log.info("Sende Buchung-Benachrichtigung fuer kontoId={}", konto.getId());
        log.debug("Buchung-Benachrichtigung Request: {}", benachrichtigungRequest);
        restTemplate.postForObject(
                "http://benachrichtigung-service:8082/api/benachrichtigungen/buchungen",
                benachrichtigungRequest,
                Void.class

        );
        log.info("Buchung-Benachrichtigung erfolgreich versendet fuer kontoId={}", konto.getId());

        return buchung;
    }

//    @Transactional
//    public Buchung create(BuchungRequest buchungRequest, boolean benachrichtigen) {
//
//        Buchung buchung = BuchungService.this.create(buchungRequest);
//        if(benachrichtigen){
//            Konto konto = kontoService.get(buchung.getKontoId());
//            String sign = buchung.getBetrag().signum() >= 0 ? "+" : "";
//            BuchungBenachrichtigungRequest request = new BuchungBenachrichtigungRequest();
//            request.setBuchungId(buchung.getId());
//            request.setKontoId(konto.getId());
//            request.setIban(konto.getIban());
//            request.setInhaber(konto.getInhaber());
//            request.setBetrag(buchung.getBetrag());
//            request.setNachricht("Buchung: " + sign + buchung.getBetrag() + " €");
//
//            log.info("Sende Buchung-Benachrichtigung fuer kontoId={} und buchungId={}", buchung.getKontoId(), buchung.getId());
//            log.debug("Buchung-Benachrichtigung Request: {}", request);
//            restTemplate.postForObject(
//                "http://benachrichtigung-service:8082/api/benachrichtigungen/buchungen",
//                request,
//                Void.class
//            );
//            log.info("Buchung-Benachrichtigung erfolgreich versendet fuer kontoId={} und buchungId={}", buchung.getKontoId(), buchung.getId());
//        }
//
//        return buchung;
//    }

    @Override
    public void delete(Long id) {
        log.info("Starte Loeschen von buchungId={}", id);
        Buchung buchung = buchungRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Buchung nicht gefunden"));
        buchungRepository.delete(buchung);
        log.info("Buchung geloescht: buchungId={}", id);
    }

    public List<Buchung> getByKontoId(Long kontoId) {
        return buchungRepository.findByKontoId(kontoId);
    }

}
