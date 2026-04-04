package com.demo.kontoservice.konto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaktionRepository extends JpaRepository<Transaktion,Long> {
    List<Transaktion> findByKontoId(Long kontoId);
}
