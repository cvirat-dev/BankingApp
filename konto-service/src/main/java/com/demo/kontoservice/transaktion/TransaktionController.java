package com.demo.kontoservice.transaktion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.kontoservice.BaseController;
import com.demo.kontoservice.CrudService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transaktionen")
@CrossOrigin(origins = "*")
public class TransaktionController extends BaseController<Transaktion, TransaktionRequest> {

    @Autowired private TransaktionService transaktionService;

    @Override
    protected CrudService<Transaktion, TransaktionRequest> service() {
        return transaktionService;
    }

    @Override
    @Operation(
        operationId = "getAllTransaktionen",
        summary = "Alle Transaktionen abrufen"
    )
    public ResponseEntity<List<Transaktion>> getAll() {
        return super.getAll();
    }
    
    @Override
    @Operation(
        operationId = "getTransaktionById",
        summary = "Transaktion nach ID abrufen"
    )
    public ResponseEntity<Transaktion> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @Operation(
        operationId = "createTransaktion",
        summary = "Neue Transaktion erstellen"
    )
    public ResponseEntity<Transaktion> create(@RequestBody @Valid TransaktionRequest request) {
        return super.create(request);
    }

    @Override
    @Operation(
        operationId = "deleteTransaktion",
        summary = "Transaktion löschen"
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }

    @GetMapping("/konto/{kontoId}")
    public List<Transaktion> getByKontoId(@PathVariable Long kontoId) {
        return transaktionService.getByKontoId(kontoId);
    }
}
