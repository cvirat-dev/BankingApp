package com.demo.kontoservice.konto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/konten")
@CrossOrigin(origins = "*") // Erlaubt Anfragen von allen Ursprüngen (für Entwicklung und Tests)
public class KontoController {
    @Autowired private KontoService kontoService;
    @Autowired private KontoRepository kontoRepository;

    @GetMapping
    public List<Konto> getAllKonten() {
        return kontoRepository.findAll();
    }

    @PostMapping
    public Konto createKonto(@RequestBody Konto konto) {
        return kontoService.createKonto(konto);
    }

    @PostMapping("/{id}/buchung")
    public Transaktion createBuchung(@PathVariable Long id, @RequestBody Transaktion transaktion) {
        return kontoService.buchung(id, transaktion.getBetrag(), transaktion.getBeschreibung());
    }

    @GetMapping("/{id}/transaktionen")
    public List<Transaktion> getTransaktionen(@PathVariable Long id) {
        return kontoService.getTransaktionen(id);
    }

    @DeleteMapping("/{id}")
    public void deleteKonto(@PathVariable Long id) {
        kontoService.deleteById(id);
    }
}
