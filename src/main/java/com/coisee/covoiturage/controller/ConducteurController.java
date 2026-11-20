package com.coisee.covoiturage.controller;

import com.coisee.covoiturage.dto.conducteur.ConducteurRequest;
import com.coisee.covoiturage.dto.conducteur.ConducteurResponse;
import com.coisee.covoiturage.service.ConducteurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conducteurs")
public class ConducteurController {

    private final ConducteurService conducteurService;

    public ConducteurController(ConducteurService conducteurService) {
        this.conducteurService = conducteurService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConducteurResponse create(
            @Valid @RequestBody ConducteurRequest request
    ) {
        return conducteurService.create(request);
    }

    @GetMapping
    public List<ConducteurResponse> findAll() {
        return conducteurService.findAll();
    }

    @GetMapping("/search")
    public List<ConducteurResponse> search(
            @RequestParam String searchTerm
    ) {
        return conducteurService.search(searchTerm);
    }

    @GetMapping("/{id}")
    public ConducteurResponse findById(@PathVariable Long id) {
        return conducteurService.findById(id);
    }

    @PutMapping("/{id}")
    public ConducteurResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ConducteurRequest request
    ) {
        return conducteurService.update(id, request);
    }
}
