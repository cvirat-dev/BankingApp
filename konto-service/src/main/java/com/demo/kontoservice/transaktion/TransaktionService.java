package com.demo.kontoservice.transaktion;

import java.util.List;

import com.demo.kontoservice.konto.Konto;
import com.demo.kontoservice.konto.KontoService;
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
    private final KontoService kontoService;
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
    public Transaktion create(TransaktionRequest transaktionRequest) {

        if (transaktionRequest == null) {
            throw new IllegalArgumentException("Transaktion darf nicht null sein.");
        }

        log.info("Starte Transaktion von kontoId={} zu kontoId={} mit betrag={}", transaktionRequest.getQuelleKontoId(), transaktionRequest.getZielKontoId(), transaktionRequest.getBetrag());
        log.debug("Eingehende Transaktion: {}", transaktionRequest);

        Transaktion newTransaktion = new Transaktion();
        newTransaktion.setQuelleKontoId(transaktionRequest.getQuelleKontoId());
        newTransaktion.setZielKontoId(transaktionRequest.getZielKontoId());
        newTransaktion.setBetrag(transaktionRequest.getBetrag());
        newTransaktion.setBeschreibung(transaktionRequest.getBeschreibung());
        newTransaktion.setTimestamp(java.time.LocalDateTime.now());
        log.debug("Persistiertes Transaktion-Objekt: {}", newTransaktion);

        BuchungRequest abbuchung = new BuchungRequest();
        abbuchung.setKontoId(newTransaktion.getQuelleKontoId());
        abbuchung.setBetrag(newTransaktion.getBetrag().negate());
        abbuchung.setBeschreibung("Überweisung an Konto " + newTransaktion.getZielKontoId() + ": " + newTransaktion.getBeschreibung());
        buchungService.create(abbuchung);

        BuchungRequest gutschrift = new BuchungRequest();
        gutschrift.setKontoId(newTransaktion.getZielKontoId());
        gutschrift.setBetrag(newTransaktion.getBetrag());
        gutschrift.setBeschreibung("Überweisung von Konto " + newTransaktion.getQuelleKontoId() + ": " + newTransaktion.getBeschreibung());
        buchungService.create(gutschrift);

        Transaktion savedTransaktion = transaktionRepository.save(newTransaktion);
        log.info("Transaktion mit id={} wurde erstellt", savedTransaktion.getId());

        TransaktionBenachrichtigungRequest request = new TransaktionBenachrichtigungRequest();
        Konto quellKonto = kontoService.get(savedTransaktion.getQuelleKontoId());
        Konto zielKonto = kontoService.get(savedTransaktion.getZielKontoId());
        request.setTransaktionId(savedTransaktion.getId());
        request.setQuelleKontoId(savedTransaktion.getQuelleKontoId());
        request.setZielKontoId(savedTransaktion.getZielKontoId());
        request.setQuelleIban(quellKonto.getIban());
        request.setZielIban(zielKonto.getIban());
        request.setQuelleInhaber(quellKonto.getInhaber());
        request.setZielInhaber(zielKonto.getInhaber());
        request.setBetrag(savedTransaktion.getBetrag());
        request.setNachricht(savedTransaktion.getBeschreibung());

        log.info("Sende Transaktion-Benachrichtigung fuer transaktionId={}", savedTransaktion.getId());
        log.debug("Transaktion-Benachrichtigung Request: {}", request);
        restTemplate.postForObject("http://benachrichtigung-service:8082/api/benachrichtigungen/transaktionen",
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
