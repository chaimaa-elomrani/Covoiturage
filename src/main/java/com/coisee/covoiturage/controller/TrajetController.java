package com.coisee.covoiturage.controller;

import com.coisee.covoiturage.dto.trajet.TrajetRequest;
import com.coisee.covoiturage.dto.trajet.TrajetResponse;
import com.coisee.covoiturage.dto.trajet.TrajetSearchRequest;
import com.coisee.covoiturage.service.TrajetService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trajets")
public class TrajetController {

    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrajetResponse publish(
            @Valid @RequestBody TrajetRequest request
    ) {
        return trajetService.publish(request);
    }

    @GetMapping("/search")
    public Page<TrajetResponse> search(
            @Valid @ModelAttribute TrajetSearchRequest request,
            Pageable pageable
    ) {
        return trajetService.search(request, pageable);
    }

    @GetMapping("/{trajetId}")
    public TrajetResponse findById(@PathVariable Long trajetId) {
        return trajetService.findById(trajetId);
    }

    @PatchMapping("/{trajetId}/finish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void finish(
            @PathVariable Long trajetId,
            @RequestParam Long conducteurId
    ) {
        trajetService.finish(trajetId, conducteurId);
    }

    @PatchMapping("/{trajetId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @PathVariable Long trajetId,
            @RequestParam Long conducteurId
    ) {
        trajetService.cancel(trajetId, conducteurId);
    }
}
