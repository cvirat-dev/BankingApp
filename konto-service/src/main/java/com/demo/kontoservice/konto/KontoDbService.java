package com.demo.kontoservice.konto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KontoDbService {

    private final KontoRepository kontoRepository;

    @Transactional
    public Konto erstelleKontoInDb(KontoCreateRequest kontoCreateRequest) {

        Konto konto = new Konto();
        konto.setId(null);

        konto.setInhaber(kontoCreateRequest.getInhaber());
        konto.setKontostand(kontoCreateRequest.getKontostand());

        // IDENTITY IDs are assigned on insert, but iban is non-null in DB.
        // Save first with a temporary value, then replace with the final IBAN based on ID.
        if (konto.getIban() == null || konto.getIban().isBlank()) {
            konto.setIban(IbanGenerator.generateTemporary());
        }
        Konto gespeichertesKonto = kontoRepository.save(konto);
        gespeichertesKonto.setIban(IbanGenerator.generate(gespeichertesKonto.getId()));
        return kontoRepository.save(gespeichertesKonto);
    }
}
