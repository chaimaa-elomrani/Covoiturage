package com.coisee.covoiturage.service;

import com.coisee.covoiturage.dto.reservation.ReservationRequest;
import com.coisee.covoiturage.dto.reservation.ReservationResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse reserve(ReservationRequest request);

    ReservationResponse findById(Long reservationId, Long passagerId);

    List<ReservationResponse> findAllByPassagerId(Long passagerId);

    List<ReservationResponse> findAllByTrajetId(
            Long trajetId,
            Long conducteurId
    );

    void cancel(Long reservationId, Long passagerId);
}
