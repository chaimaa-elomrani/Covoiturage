package com.coisee.covoiturage.repository;

import com.coisee.covoiturage.entity.PointArret;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface PointArretRepository extends JpaRepository<PointArret, Long> {

    Optional<PointArret> findByIdAndTrajetId(Long pointArretId, Long trajetId);

    List<PointArret> findAllByTrajetIdOrderByOrdreAsc(Long trajetId);
}
