package com.demo.kontoservice.konto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaktionRepository extends JpaRepository<Transaktion,Long> {
    
    List<Transaktion> findByKontoId(Long kontoId);
    void deleteByKontoId(Long kontoId);
}
