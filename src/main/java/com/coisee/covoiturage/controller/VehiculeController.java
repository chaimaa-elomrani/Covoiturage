package com.coisee.covoiturage.controller;

import com.coisee.covoiturage.dto.vehicule.VehiculeRequest;
import com.coisee.covoiturage.dto.vehicule.VehiculeResponse;
import com.coisee.covoiturage.service.VehiculeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeResponse create(
            @Valid @RequestBody VehiculeRequest request
    ) {
        return vehiculeService.create(request);
    }

    @GetMapping("/{vehiculeId}")
    public VehiculeResponse findById(
            @PathVariable Long vehiculeId,
            @RequestParam Long conducteurId
    ) {
        return vehiculeService.findById(vehiculeId, conducteurId);
    }

    @GetMapping("/conducteur/{conducteurId}")
    public List<VehiculeResponse> findAllByConducteurId(
            @PathVariable Long conducteurId
    ) {
        return vehiculeService.findAllByConducteurId(conducteurId);
    }

    @PutMapping("/{vehiculeId}")
    public VehiculeResponse update(
            @PathVariable Long vehiculeId,
            @RequestParam Long conducteurId,
            @Valid @RequestBody VehiculeRequest request
    ) {
        return vehiculeService.update(vehiculeId, conducteurId, request);
    }

    @DeleteMapping("/{vehiculeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long vehiculeId,
            @RequestParam Long conducteurId
    ) {
        vehiculeService.supprimer(vehiculeId, conducteurId);
    }
}
