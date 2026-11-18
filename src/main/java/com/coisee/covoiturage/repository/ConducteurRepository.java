package com.coisee.covoiturage.repository;

import com.coisee.covoiturage.entity.Conducteur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConducteurRepository extends JpaRepository<Conducteur, Long> {

    boolean  existsByEmailIgnoreCase(String email);

    boolean existsByNumeroPermisIgnoreCase(String numeroPermis);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByNumeroPermisIgnoreCaseAndIdNot(
            String numeroPermis,
            Long id
    );
    List<Conducteur> findByNomContainingIgnoreCaseOrEmailIgnoreCase(
            String nom,
            String email
    );
}
