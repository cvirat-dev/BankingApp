package com.demo.kontoservice.konto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.demo.kontoservice.BaseController;
import com.demo.kontoservice.CrudService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/konten")
@CrossOrigin(origins = "*")
public class KontoController extends BaseController<Konto, KontoCreateRequest> {
    
    @Autowired private KontoService kontoService;

    @Override
    protected CrudService<Konto, KontoCreateRequest> service() {
        return kontoService;
    }

    @Override
    @Operation(
        operationId = "getAllKonten",
        summary = "Alle Konten abrufen"
    )
    public ResponseEntity<List<Konto>> getAll() {
        return super.getAll();
    }

    @Override
    @Operation(
        operationId = "getKontoById",
        summary = "Konto nach ID abrufen"
    )
    public ResponseEntity<Konto> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @Operation(
        operationId = "createKonto",
        summary = "Neues Konto erstellen"
    )
    public ResponseEntity<Konto> create(@RequestBody @Valid KontoCreateRequest request) {
        return super.create(request);
    }

    @PutMapping("/{id}")
    @Operation(
        operationId = "updateKonto",
        summary = "Konto aktualisieren"
    )
    public ResponseEntity<Konto> update(
            @PathVariable Long id,
            @RequestBody @Valid KontoUpdateRequest request) {
        return ResponseEntity.ok(kontoService.update(id, request));
    }

    @Override
    @Operation(
        operationId = "deleteKonto",
            summary = "Konto löschen"
        )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }
}
