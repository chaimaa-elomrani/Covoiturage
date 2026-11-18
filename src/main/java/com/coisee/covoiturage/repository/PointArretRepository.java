package com.coisee.covoiturage.repository;

import com.coisee.covoiturage.entity.PointArret;

import java.util.List;
import java.util.Optional;
public interface PointArretRepository {

    Optional<PointArret> findByIdAndTrajetId(Long pointArretId, Long trajetId);

    List<PointArret> findAllByTrajetIdOrderByOrdreAsc(Long trajetId);
}
