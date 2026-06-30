package com.demo.kontoservice.konto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.demo.kontoservice.CrudService;
import com.demo.kontoservice.benachrichtigung.KontoBenachrichtigungRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KontoService implements CrudService<Konto, KontoRequest> {
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
    public Konto create(KontoRequest kontoRequest) {
        log.info("Starte Kontoerstellung fuer Inhaber={}", kontoRequest.getInhaber());
        log.debug("Eingehendes Konto fuer Erstellung: {}", kontoRequest);

        Konto gespeichertesKonto = kontoDbService.erstelleKontoInDb(kontoRequest);
        log.info(
            "Konto erfolgreich erstellt: kontoId={}, iban={}, inhaber={}",
            gespeichertesKonto.getId(),
            gespeichertesKonto.getIban(),

            gespeichertesKonto.getInhaber()
        );
        log.debug("Persistiertes Kontoobjekt: {}", gespeichertesKonto);

        // FAT-Event (Microservices konform)
        KontoBenachrichtigungRequest request = new KontoBenachrichtigungRequest();
        request.setKontoId(gespeichertesKonto.getId());
        request.setIban(gespeichertesKonto.getIban());
        request.setInhaber(gespeichertesKonto.getInhaber());
        request.setNachricht("Neues Konto erstellt: " + gespeichertesKonto.getInhaber());
        log.info("Sende Konto-Benachrichtigung fuer kontoId={}", gespeichertesKonto.getId());
        log.debug("Konto-Benachrichtigung Request: {}", request);
        restTemplate.postForObject(
            "http://benachrichtigung-service:8082/api/benachrichtigungen/konto",
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
}
