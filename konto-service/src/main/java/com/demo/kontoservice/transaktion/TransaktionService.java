package com.demo.kontoservice.transaktion;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.demo.kontoservice.CrudService;
import com.demo.kontoservice.benachrichtigung.TransaktionBenachrichtigungRequest;
import com.demo.kontoservice.buchung.BuchungRequest;
import com.demo.kontoservice.buchung.BuchungService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransaktionService implements CrudService<Transaktion, TransaktionRequest> {

    private final TransaktionRepository transaktionRepository;
    
    private final BuchungService buchungService;
    
    private final RestTemplate restTemplate;

    @Override
    public List<Transaktion> getAll() {
        return transaktionRepository.findAll();
    }

    public List<Transaktion> getByKontoId(Long kontoId) {
        return transaktionRepository.findByQuelleKontoIdOrZielKontoId(kontoId, kontoId);
    }

    @Override
    public Transaktion get(Long id) {
        return transaktionRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Transaktion nicht gefunden"));
    }

    @Override
    @Transactional
    public Transaktion create(TransaktionRequest transaktion) {

        if (transaktion == null) {
            throw new IllegalArgumentException("Transaktion darf nicht null sein.");
        }
        if (transaktion.getQuelleKontoId() == null || transaktion.getZielKontoId() == null) {
            throw new IllegalArgumentException("Quell- und Zielkonto müssen gesetzt sein.");
        }
        if (transaktion.getQuelleKontoId().equals(transaktion.getZielKontoId())) {
            throw new IllegalArgumentException("Quell- und Zielkonto dürfen nicht identisch sein.");
        }
        if (transaktion.getBetrag() == null || transaktion.getBetrag().signum() <= 0) {
            throw new IllegalArgumentException("Betrag muss größer als 0 sein.");
        }

        log.info("Starte Transaktion von kontoId={} zu kontoId={} mit betrag={}", transaktion.getQuelleKontoId(), transaktion.getZielKontoId(), transaktion.getBetrag());
        Transaktion newTransaktion = new Transaktion();
        newTransaktion.setQuelleKontoId(transaktion.getQuelleKontoId());
        newTransaktion.setZielKontoId(transaktion.getZielKontoId());
        newTransaktion.setBetrag(transaktion.getBetrag());
        newTransaktion.setBeschreibung(transaktion.getBeschreibung());
        newTransaktion.setTimestamp(java.time.LocalDateTime.now());

        BuchungRequest abbuchung = new BuchungRequest();
        abbuchung.setKontoId(newTransaktion.getQuelleKontoId());
        abbuchung.setBetrag(newTransaktion.getBetrag().negate());
        abbuchung.setBeschreibung("Überweisung an Konto " + newTransaktion.getZielKontoId() + ": " + newTransaktion.getBeschreibung());
        buchungService.create(abbuchung, false);

        BuchungRequest gutschrift = new BuchungRequest();
        gutschrift.setKontoId(newTransaktion.getZielKontoId());
        gutschrift.setBetrag(newTransaktion.getBetrag());
        gutschrift.setBeschreibung("Überweisung von Konto " + newTransaktion.getQuelleKontoId() + ": " + newTransaktion.getBeschreibung());
        buchungService.create(gutschrift, false);

        Transaktion savedTransaktion = transaktionRepository.save(newTransaktion);
        log.info("Transaktion mit id={} wurde erstellt", savedTransaktion.getId());

        TransaktionBenachrichtigungRequest request = new TransaktionBenachrichtigungRequest();
        request.setQuelleKontoId(savedTransaktion.getQuelleKontoId());
        request.setZielKontoId(savedTransaktion.getZielKontoId());
        request.setBetrag(savedTransaktion.getBetrag());
        request.setNachricht(savedTransaktion.getBeschreibung());

        log.info("Sende Transaktion-Benachrichtigung fuer transaktionId={}", savedTransaktion.getId());
        log.debug("Transaktion-Benachrichtigung Request: {}", request);
        restTemplate.postForObject("http://benachrichtigung-service:8082/api/benachrichtigungen/transaktion",
            request,
            Void.class
        );
        log.info("Transaktion-Benachrichtigung erfolgreich versendet fuer transaktionId={}", savedTransaktion.getId());

        return savedTransaktion;
    }

    @Override
    public void delete(Long id) {
        log.info("Lösche Transaktion mit id={}", id);
        Transaktion transaktion = get(id);
        transaktionRepository.delete(transaktion);
        log.info("Transaktion mit id={} wurde gelöscht", id);
    }

}
