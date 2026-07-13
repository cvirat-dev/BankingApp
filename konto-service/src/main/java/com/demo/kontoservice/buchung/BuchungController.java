package com.demo.kontoservice.buchung;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/buchungen")
@CrossOrigin(origins = "*")
public class BuchungController extends BaseController<Buchung, BuchungRequest> {

    @Autowired
    private BuchungService buchungService;

    @Override
    protected CrudService<Buchung, BuchungRequest> service() {
        return buchungService;
    }

    @Override
    @Operation(
        operationId = "getAllBuchungen",
        summary = "Alle Buchungen abrufen"
    )
    public ResponseEntity<List<Buchung>> getAll() {
        return super.getAll();
    }

    @Override
    @Operation(
        operationId = "getBuchungById",
        summary = "Buchung nach ID abrufen"
    )
    public ResponseEntity<Buchung> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @Operation(
        operationId = "createBuchung",
        summary = "Neue Buchung erstellen"
    )
    public ResponseEntity<Buchung> create(@RequestBody @Valid BuchungRequest request) {
        return super.create(request);
    }

    @Override
    @Operation(
        operationId = "deleteBuchung",
        summary = "Buchung löschen"
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }


    @Operation(
        operationId = "getBuchungenByKontoId",
        summary = "Buchungen nach Konto-ID abrufen"
    )
    @GetMapping(value = "/konto/{kontoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Buchung> getByKontoId(@PathVariable Long kontoId) {
        return buchungService.getByKontoId(kontoId);
    }
}
