package com.demo.kontoservice.transaktion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaktionRepository extends JpaRepository<Transaktion, Long> {

    List<Transaktion> findByQuelleKontoId(Long quelleKontoId);
    List<Transaktion> findByZielKontoId(Long zielKontoId);
    List<Transaktion> findByQuelleKontoIdOrZielKontoId(Long quelleKontoId, Long zielKontoId);
    void deleteByQuelleKontoIdOrZielKontoId(Long quelleKontoId, Long zielKontoId);
}
