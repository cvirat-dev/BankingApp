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
        Konto gespeichertesKonto = kontoRepository.save(konto);
        gespeichertesKonto.setIban(IbanGenerator.generate(gespeichertesKonto.getId()));
        return kontoRepository.save(gespeichertesKonto);
    }
}
