package com.demo.kontoservice.buchung;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BuchungRepository extends JpaRepository<Buchung,Long> {
    
    List<Buchung> findByKontoId(Long kontoId);
    void deleteByKontoId(Long kontoId);
}
