package com.coisee.covoiturage.repository;

import com.coisee.covoiturage.entity.Trajet;

import java.util.Optional;

public interface TrajetRepository {

    Optional<Trajet> findByIdAndConducteurId(Long trajetId, Long conducteurId);

    boolean existsByVehiculeId(Long vehiculeId);
}
