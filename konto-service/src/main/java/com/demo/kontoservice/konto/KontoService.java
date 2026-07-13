package com.demo.kontoservice.konto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.demo.kontoservice.UpdatableCrudService;
import com.demo.kontoservice.benachrichtigung.Aktion;
import com.demo.kontoservice.benachrichtigung.KontoBenachrichtigungRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KontoService implements UpdatableCrudService<Konto, KontoCreateRequest, KontoUpdateRequest> {
    @Autowired 
    private KontoRepository kontoRepository;
    
    @Autowired 
    private KontoDbService kontoDbService;
    
    @Autowired
    private RestTemplate restTemplate;

    public void save(Konto konto) {
        log.info("Aktualisiere Konto fuer kontoId={}", konto.getId());
        log.debug("Konto vor Update: {}", konto);
        kontoRepository.save(konto);
        log.info("Konto erfolgreich aktualisiert: kontoId={}", konto.getId());
        log.debug("Konto nach Update: {}", konto);
    }

    @Override
    public List<Konto> getAll() {
        log.info("Lade alle Konten");
        List<Konto> konten = kontoRepository.findAll();
        log.info("Alle Konten geladen: anzahl={}", konten.size());
        return konten;
    }

    @Override
    public Konto get(Long id) {
        log.info("Lade Konto fuer kontoId={}", id);
        Konto konto = kontoRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Konto nicht gefunden"));
        log.debug("Konto geladen: {}", konto);
        return konto;
    }

    @Override
    public Konto create(KontoCreateRequest kontoCreateRequest) {
        log.info("Starte Kontoerstellung fuer Inhaber={}", kontoCreateRequest.getInhaber());
        log.debug("Eingehendes Konto fuer Erstellung: {}", kontoCreateRequest);

        Konto gespeichertesKonto = kontoDbService.erstelleKontoInDb(kontoCreateRequest);
        log.info(
            "Konto erfolgreich erstellt: kontoId={}, iban={}, inhaber={}",
            gespeichertesKonto.getId(),
            gespeichertesKonto.getIban(),

            gespeichertesKonto.getInhaber()
        );
        log.debug("Persistiertes Kontoobjekt: {}", gespeichertesKonto);

        // FAT-Event (Microservices konform)
        KontoBenachrichtigungRequest request = new KontoBenachrichtigungRequest();
        request.setAktion(Aktion.ERSTELLEN);
        request.setKontoId(gespeichertesKonto.getId());
        request.setIban(gespeichertesKonto.getIban());
        request.setInhaber(gespeichertesKonto.getInhaber());
        request.setNachricht("Neues Konto erstellt: " + gespeichertesKonto.getInhaber());
        log.info("Sende Konto-Benachrichtigung fuer kontoId={}", gespeichertesKonto.getId());
        log.debug("Konto-Benachrichtigung Request: {}", request);
        restTemplate.postForObject(
            "http://benachrichtigung-service:8082/api/benachrichtigungen/konten",
            request,
            Void.class
        );
        log.info("Konto-Benachrichtigung erfolgreich versendet fuer kontoId={}", gespeichertesKonto.getId());
        
        return gespeichertesKonto;
    }

    @Override
    public void delete(Long id) {
        log.info("Starte Loeschen von kontoId={}", id);

        kontoRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Konto nicht gefunden"));
        log.debug("Konto fuer Loeschung gefunden: kontoId={}", id);

        kontoRepository.deleteById(id);
        log.info("Konto erfolgreich geloescht: kontoId={}", id);
    }

    @Override
    public Konto update(Long id, KontoUpdateRequest kontoUpdateRequest) {
        log.info("Starte Update von kontoId={}", id);
        log.debug("Eingehendes Konto fuer Update: {}", kontoUpdateRequest);

        Konto konto = kontoRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Konto nicht gefunden"));
        log.debug("Konto fuer Update gefunden: {}", konto);

        konto.setInhaber(kontoUpdateRequest.getInhaber()); 
        // Kontostand wird nicht direkt aktualisiert, da er durch Transaktionen beeinflusst wird
        save(konto);

        return konto;
    }
}
