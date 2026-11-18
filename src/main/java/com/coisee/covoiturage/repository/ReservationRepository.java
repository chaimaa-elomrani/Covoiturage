package com.coisee.covoiturage.repository;

import com.coisee.covoiturage.entity.Passager;
import com.coisee.covoiturage.entity.Reservation;
import com.coisee.covoiturage.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation , Long> {

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
