package com.coisee.covoiturage.repository;

import com.coisee.covoiturage.entity.Trajet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {


    Optional<Trajet> findByIdAndConducteurId(Long trajetId, Long conducteurId);

    boolean existsByVehiculeId(Long vehiculeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Trajet t where t.id = :id")
    Optional<Trajet> findByIdForUpdate(@Param("id") Long id);
}
