package com.coisee.covoiturage.repository;

import com.coisee.covoiturage.entity.Passager;
import com.coisee.covoiturage.entity.Reservation;
import com.coisee.covoiturage.enums.StatutReservation;

import java.util.List;

public interface ReservationRepository {

    List<Reservation> findAllByPassagerIdOrderByDateReservationDesc(
            Long passagerId
    );

    List<Reservation> findAllByTrajetId(Long trajetId);

    boolean existsByPassagerIdAndTrajetIdAndStatut(
            Long passagerId,
            Long trajetId,
            StatutReservation statut
    );
}
