package com.demo.kontoservice.buchung;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.kontoservice.BaseController;

@RestController
@RequestMapping("/api/buchungen")
@CrossOrigin(origins = "*")
public class BuchungController extends BaseController<Buchung, BuchungRequest> {

    @Autowired
    private BuchungService buchungService;

    @Override
    protected List<Buchung> findAll() {
        return buchungService.getAll();
    }

    @Override
    protected Buchung findById(Long id) {
        return buchungService.get(id);
    }

    @Override
    protected Buchung createEntity(BuchungRequest request) {
        return buchungService.create(request, true);
    }

    @Override
    protected void deleteById(Long id) {
        buchungService.delete(id);
    }

    @GetMapping("/konto/{kontoId}")
    public List<Buchung> getByKontoId(@PathVariable Long kontoId) {
        return buchungService.getBuchungen(kontoId);
    }
}
