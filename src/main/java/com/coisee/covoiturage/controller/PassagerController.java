package com.coisee.covoiturage.controller;

import com.coisee.covoiturage.dto.passager.PassagerRequest;
import com.coisee.covoiturage.dto.passager.PassagerResponse;
import com.coisee.covoiturage.service.PassagerService;
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
@RequestMapping("/api/passagers")
public class PassagerController {

    private final PassagerService passagerService;

    public PassagerController(PassagerService passagerService) {
        this.passagerService = passagerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassagerResponse create(
            @Valid @RequestBody PassagerRequest request
    ) {
        return passagerService.create(request);
    }

    @GetMapping
    public List<PassagerResponse> findAll() {
        return passagerService.findAll();
    }

    @GetMapping("/search")
    public PassagerResponse findByEmail(@RequestParam String email) {
        return passagerService.findByEmail(email);
    }

    @GetMapping("/{id}")
    public PassagerResponse findById(@PathVariable Long id) {
        return passagerService.findById(id);
    }

    @PutMapping("/{id}")
    public PassagerResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PassagerRequest request
    ) {
        return passagerService.update(id, request);
    }
}
