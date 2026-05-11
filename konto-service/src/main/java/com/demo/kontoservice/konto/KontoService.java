package com.demo.kontoservice.konto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class KontoService {
    @Autowired private KontoRepository kontoRepository;
    @Autowired private TransaktionRepository transaktionRepository;
    @Autowired private RestTemplate restTemplate;

    public Konto createKonto(Konto konto) {
        konto.setId(null);
        return kontoRepository.save(konto);
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

    @Transactional
    public void deleteById(Long id) {

        kontoRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Konto nicht gefunden"));

        transaktionRepository.deleteByKontoId(id);
        kontoRepository.deleteById(id);
    }
}
