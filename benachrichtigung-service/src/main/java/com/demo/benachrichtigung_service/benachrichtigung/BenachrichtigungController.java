package com.demo.benachrichtigung_service.benachrichtigung;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/benachrichtigungen")
@CrossOrigin(origins = "*")
public class BenachrichtigungController {

    @Autowired
    private BenachrichtigungRepository repo;

    @PostMapping
    public Benachrichtigung empfangen(@RequestBody Map<String, String> body) {
        Benachrichtigung b = new Benachrichtigung();
        b.setNachricht(body.get("nachricht"));
        b.setTimestamp(LocalDateTime.now());
        return repo.save(b);
    }

    @GetMapping
    public List<Benachrichtigung> all() { return repo.findAll(); }
}
