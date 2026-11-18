package com.coisee.covoiturage.service;

import com.coisee.covoiturage.dto.vehicule.VehiculeRequest;
import com.coisee.covoiturage.dto.vehicule.VehiculeResponse;

import java.util.List;

public interface VehiculeService {

    VehiculeResponse create(VehiculeRequest request);

    VehiculeResponse findById(Long vehiculeId, Long conducteurId);

    List<VehiculeResponse> findAllByConducteurId(Long conducteurId);

    VehiculeResponse update(
            Long vehiculeId,
            Long conducteurId,
            VehiculeRequest request
    );

    void supprimer(Long vehiculeId, Long conducteurId);
}