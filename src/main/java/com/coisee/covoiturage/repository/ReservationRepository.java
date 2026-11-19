package com.coisee.covoiturage.repository;

import com.coisee.covoiturage.entity.Reservation;
import com.coisee.covoiturage.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
        select coalesce(sum(r.nombrePlace), 0)
        from Reservation r 
        where r.trajet.id = :trajetId
        and r.statut = :statut 
""")
    Long sumNombrePlaceByTrajetIdAndStatus(
            @Param("trajetId") Long trajetId ,
            @Param("statut") StatutReservation statut
    );
}
