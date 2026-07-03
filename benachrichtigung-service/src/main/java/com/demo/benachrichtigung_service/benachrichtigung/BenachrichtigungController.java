package com.demo.benachrichtigung_service.benachrichtigung;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/benachrichtigungen")
@CrossOrigin(origins = "*")
public class BenachrichtigungController {

    @Autowired private BenachrichtigungService benachrichtigungService;

    @Operation(summary = "Empfängt eine Konto-Benachrichtigung und speichert sie in der Datenbank.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Benachrichtigung erfolgreich empfangen und gespeichert."),
        @ApiResponse(responseCode = "400", description = "Ungültige Anfrage. Überprüfen Sie die übermittelten Daten.")
    })
    @PostMapping("/konten")
    @ResponseStatus(HttpStatus.CREATED)
    public KontoBenachrichtigung receiveKonto(@Valid @RequestBody KontoBenachrichtigungRequest request) {
        return benachrichtigungService.receive(request);
    }

    @Operation(summary = "Empfängt eine Buchung-Benachrichtigung und speichert sie in der Datenbank.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Benachrichtigung erfolgreich empfangen und gespeichert."),
        @ApiResponse(responseCode = "400", description = "Ungültige Anfrage. Überprüfen Sie die übermittelten Daten.")
    })
    @PostMapping("/buchungen")
    @ResponseStatus(HttpStatus.CREATED)
    public BuchungBenachrichtigung receiveBuchung(@Valid @RequestBody BuchungBenachrichtigungRequest request) {
        return benachrichtigungService.receive(request);
    }

    @Operation(summary = "Empfängt eine Transaktion-Benachrichtigung und speichert sie in der Datenbank.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Benachrichtigung erfolgreich empfangen und gespeichert."),
        @ApiResponse(responseCode = "400", description = "Ungültige Anfrage. Überprüfen Sie die übermittelten Daten.")
    })
    @PostMapping("/transaktionen")
    @ResponseStatus(HttpStatus.CREATED)
    public TransaktionBenachrichtigung receiveTransaktion(@Valid @RequestBody TransaktionBenachrichtigungRequest request) {
        return benachrichtigungService.receive(request);
    }

    @Operation(summary = "Gibt alle Konto-Benachrichtigungen zurück, optional gefiltert nach verschiedenen Kriterien.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Erfolgreich alle Konto-Benachrichtigungen abgerufen."),
        @ApiResponse(responseCode = "400", description = "Ungültige Anfrage. Überprüfen Sie die übermittelten Parameter.")
    })
    @GetMapping("/konten")
    public List<KontoBenachrichtigung> allKonto(
        @RequestParam(required = false) Long kontoId,
        @RequestParam(required = false) String iban,
        @RequestParam(required = false) String inhaber,
        @RequestParam(required = false) AktionTyp aktion,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime von,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bis
    ) {
        return benachrichtigungService.allKonto(kontoId, iban, inhaber, aktion, von, bis);
    }

    @Operation(summary = "Gibt alle Buchung-Benachrichtigungen zurück, optional gefiltert nach verschiedenen Kriterien.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Erfolgreich alle Buchung-Benachrichtigungen abgerufen."),
        @ApiResponse(responseCode = "400", description = "Ungültige Anfrage. Überprüfen Sie die übermittelten Parameter.")
    })
    @GetMapping("/buchungen")
    public List<BuchungBenachrichtigung> allBuchungen(
        @RequestParam(required = false) Long buchungId,
        @RequestParam(required = false) Long kontoId,
        @RequestParam(required = false) String iban,
        @RequestParam(required = false) String inhaber,
        @RequestParam(required = false) Double betrag,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime von,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bis
    ) {
        return benachrichtigungService.allBuchung(buchungId, kontoId, iban, inhaber, betrag, von, bis);
    }

    @Operation(summary = "Gibt alle Transaktion-Benachrichtigungen zurück, optional gefiltert nach verschiedenen Kriterien.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Erfolgreich alle Transaktion-Benachrichtigungen abgerufen."),
        @ApiResponse(responseCode = "400", description = "Ungültige Anfrage. Überprüfen Sie die übermittelten Parameter.")
    })
    @GetMapping("/transaktionen")
    public List<TransaktionBenachrichtigung> allTransaktionen(
        @RequestParam(required = false) Long transaktionId,
        @RequestParam(required = false) Long quelleKontoId,
        @RequestParam(required = false) Long zielKontoId,
        @RequestParam(required = false) String quelleIban,
        @RequestParam(required = false) String zielIban,
        @RequestParam(required = false) String quelleInhaber,
        @RequestParam(required = false) String zielInhaber,
        @RequestParam(required = false) Double betrag,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime von,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bis
    ) {
        return benachrichtigungService.allTransaktion(
                transaktionId,
                quelleKontoId,
                zielKontoId,
                quelleIban,
                zielIban,
                quelleInhaber,
                zielInhaber,
                betrag,
                von,
                bis
        );
    }

    @Operation(summary = "Gibt alle Benachrichtigungen zurück, optional gefiltert nach verschiedenen Kriterien.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Erfolgreich alle Benachrichtigungen abgerufen."),
        @ApiResponse(responseCode = "400", description = "Ungültige Anfrage. Überprüfen Sie die übermittelten Parameter.")
    })
    @GetMapping
    public List<Benachrichtigung> all(
        @RequestParam(required = false) BenachrichtigungTyp typ,
        @RequestParam(required = false) Long kontoId,
        @RequestParam(required = false) Long buchungId,
        @RequestParam(required = false) Long transaktionId,
        @RequestParam(required = false) Long quelleKontoId,
        @RequestParam(required = false) Long zielKontoId,
        @RequestParam(required = false) String iban,
        @RequestParam(required = false) String quelleIban,
        @RequestParam(required = false) String zielIban,
        @RequestParam(required = false) String inhaber,
        @RequestParam(required = false) String quelleInhaber,
        @RequestParam(required = false) String zielInhaber,
        @RequestParam(required = false) AktionTyp aktion,
        @RequestParam(required = false) Double betrag,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime von,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bis
    ) {
        return benachrichtigungService.all(
                typ,
                kontoId,
                buchungId,
                transaktionId,
                quelleKontoId,
                zielKontoId,
                iban,
                quelleIban,
                zielIban,
                inhaber,
                quelleInhaber,
                zielInhaber,
                aktion,
                betrag,
                von,
                bis
        );
    }
}
