package com.demo.kontoservice;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

public abstract class BaseController<TEntity, TCreateRequest> {

    protected abstract CrudService<TEntity, TCreateRequest> service();

    @Operation(summary = "Alle Einträge abrufen")
    @ApiResponse(responseCode = "200", description = "Erfolgreich abgerufen")
    @GetMapping
    public ResponseEntity<List<TEntity>> getAll() {
        return ResponseEntity.ok(service().getAll());
    }
    
    @Operation(summary = "Eintrag nach ID abrufen")
    @ApiResponse(responseCode = "200", description = "Erfolgreich abgerufen")
    @ApiResponse(responseCode = "404", description = "Eintrag nicht gefunden")
    @GetMapping("/{id}")
    public ResponseEntity<TEntity> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service().get(id));
    }

    @Operation(summary = "Neuen Eintrag erstellen")
    @ApiResponse(responseCode = "201", description = "Erfolgreich erstellt")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabe")
    @PostMapping
    public ResponseEntity<TEntity> create(@Valid @RequestBody TCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service().create(request));
    }
    
    @Operation(summary = "Eintrag löschen")
    @ApiResponse(responseCode = "204", description = "Erfolgreich gelöscht")
    @ApiResponse(responseCode = "404", description = "Eintrag nicht gefunden")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service().delete(id);
        return ResponseEntity.noContent().build();
    }

}
