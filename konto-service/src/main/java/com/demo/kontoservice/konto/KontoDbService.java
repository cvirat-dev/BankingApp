package com.demo.kontoservice.konto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KontoDbService {

    @Autowired
    private KontoRepository kontoRepository;

    @Transactional
    public Konto erstelleKontoInDb(Konto konto) {
        konto.setId(null);
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
