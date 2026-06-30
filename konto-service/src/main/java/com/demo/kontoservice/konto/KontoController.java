package com.demo.kontoservice.konto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.kontoservice.BaseController;

@RestController
@RequestMapping("/api/konten")
@CrossOrigin(origins = "*")
public class KontoController extends BaseController<Konto, KontoRequest> {
    
    @Autowired private KontoService kontoService;

    @Override
    protected List<Konto> findAll() {
        return kontoService.getAll();
    }

    @Override
    protected Konto findById(Long id) {
        return kontoService.get(id);
    }

    @Override
    protected Konto createEntity(KontoRequest kontoRequest) {
        return kontoService.create(kontoRequest);
    }

    @Override
    protected void deleteById(Long id) {
        kontoService.delete(id);
    }
}
