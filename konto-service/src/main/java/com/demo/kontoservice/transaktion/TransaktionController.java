package com.demo.kontoservice.transaktion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.kontoservice.BaseController;

@RestController
@RequestMapping("/api/transaktionen")
@CrossOrigin(origins = "*")
public class TransaktionController extends BaseController<Transaktion, TransaktionRequest> {

    @Autowired private TransaktionService transaktionService;

    @Override
    protected List<Transaktion> findAll() {
        return transaktionService.getAll();
    }

    @Override
    protected Transaktion findById(Long id) {
        return transaktionService.get(id);
    }

    @Override
    protected Transaktion createEntity(TransaktionRequest request) {
        return transaktionService.create(request);
    }

    @Override
    protected void deleteById(Long id) {
        transaktionService.delete(id);
    }

    @GetMapping("/konto/{kontoId}")
    public List<Transaktion> getByKontoId(@PathVariable Long kontoId) {
        return transaktionService.getByKontoId(kontoId);
    }
}
