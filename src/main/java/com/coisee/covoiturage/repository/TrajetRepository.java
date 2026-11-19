package com.coisee.covoiturage.repository;

import com.coisee.covoiturage.entity.Trajet;
import com.coisee.covoiturage.entity.Reservation;
import com.coisee.covoiturage.enums.StatutReservation;
import com.coisee.covoiturage.enums.StatutTrajet;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {


    Optional<Trajet> findByIdAndConducteurId(Long trajetId, Long conducteurId);

    boolean existsByVehiculeId(Long vehiculeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Trajet t where t.id = :id")
    Optional<Trajet> findByIdForUpdate(@Param("id") Long id);


    @Query(
            value = """
        select t from Trajet t 
        join t.pointsArret depart
        join t.pointsArret arrivee
        where t.statut = :statut 
        and depart.ville  = :villeDepart
        and arrivee.ville = :villeArrivee
        and depart.ordre  < arrivee.ordre 
        and depart.heurePassage >= :debutJournee 
        and depart.heurePassage < :lendemain 
        and (
                t.placesProposees - coalesce(
                (
                    select sum(r.nombrePlaces)
                    from Reservation r 
                    where r.trajet = t 
                    and r.statut = :statutReservation 
                ), 0
                )
            ) >= :nombrePlaces 
        and (:prixMax is null or arrivee.prixDepuisDepart - depart.prixDepuisDepart <= :prixMax)
        order by depart.heurePassage asc
        """,
            countQuery = """
        select count(distinct t.id)
        from Trajet t
        join t.pointsArret depart
        join t.pointsArret arrivee
        where t.statut = :statut
          and depart.ville = :villeDepart
          and arrivee.ville = :villeArrivee
          and depart.ordre < arrivee.ordre
          and depart.heurePassage >= :debutJournee
          and depart.heurePassage < :lendemain
          and (
              t.placesProposees - coalesce(
                  (
                      select sum(r.nombrePlaces)
                      from Reservation r
                      where r.trajet = t
                        and r.statut = :statutReservation
                  ),
                  0
              )
          ) >= :nombrePlaces
          and (:prixMax is null or arrivee.prixDepuisDepart - depart.prixDepuisDepart <= :prixMax)
        """
    )

    Page<Trajet> search(
            @Param("villeDepart") String villeDepart,
            @Param("villeArrivee") String villeArrivee,
            @Param("debutJournee") LocalDateTime debutJournee,
            @Param("lendemain") LocalDateTime lendemain,
            @Param("nombrePlaces") Integer nombrePlaces,
            @Param("prixMax") BigDecimal prixMax,
            @Param("statut") StatutTrajet statut,
            @Param("statutReservation") StatutReservation statutReservation,
            Pageable pageable
    );
}
