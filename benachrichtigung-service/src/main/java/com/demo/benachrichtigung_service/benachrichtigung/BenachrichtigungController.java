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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/benachrichtigungen")
@CrossOrigin(origins = "*")
public class BenachrichtigungController {

    @Autowired private BenachrichtigungService benachrichtigungService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Benachrichtigung receive(@Valid @RequestBody BenachrichtigungRequest request) {
        return benachrichtigungService.receive(request);
    }

    @GetMapping
    public List<Benachrichtigung> all(
        @RequestParam(required = false) BenachrichtigungTyp typ,
        @RequestParam(required = false) String iban,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime von,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bis
    ) {
        return benachrichtigungService.all(typ, iban, von, bis);
    }
}
