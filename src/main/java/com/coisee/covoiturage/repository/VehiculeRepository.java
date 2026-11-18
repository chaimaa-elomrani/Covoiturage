package com.coisee.covoiturage.repository;

import com.coisee.covoiturage.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculeRepository  extends JpaRepository<Vehicule, Long> {

    boolean existsByImmatriculationIgnoreCase(String immatriculation);

    boolean existsByImmatriculationIgnoreCaseAndIdNot(
            String immatriculation,
            Long id
    );

    List<Vehicule> findAllByConducteurId(Long conducteurId);

    Optional<Vehicule> findByIdAndConducteurId(
            Long vehiculeId,
            Long conducteurId
    );
}
