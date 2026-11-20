package com.coisee.covoiturage.controller;

import com.coisee.covoiturage.dto.reservation.ReservationRequest;
import com.coisee.covoiturage.dto.reservation.ReservationResponse;
import com.coisee.covoiturage.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse reserve(
            @Valid @RequestBody ReservationRequest request
    ) {
        return reservationService.reserve(request);
    }

    @GetMapping("/{reservationId}")
    public ReservationResponse findById(
            @PathVariable Long reservationId,
            @RequestParam Long passagerId
    ) {
        return reservationService.findById(reservationId, passagerId);
    }

    @GetMapping("/passager/{passagerId}")
    public List<ReservationResponse> findAllByPassagerId(
            @PathVariable Long passagerId
    ) {
        return reservationService.findAllByPassagerId(passagerId);
    }

    @GetMapping("/trajet/{trajetId}")
    public List<ReservationResponse> findAllByTrajetId(
            @PathVariable Long trajetId,
            @RequestParam Long conducteurId
    ) {
        return reservationService.findAllByTrajetId(trajetId, conducteurId);
    }

    @PatchMapping("/{reservationId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @PathVariable Long reservationId,
            @RequestParam Long passagerId
    ) {
        reservationService.cancel(reservationId, passagerId);
    }
}
