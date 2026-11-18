package com.coisee.covoiturage.service;

import com.coisee.covoiturage.dto.trajet.TrajetRequest;
import com.coisee.covoiturage.dto.trajet.TrajetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrajetService {

    TrajetResponse publish(TrajetRequest request);

    TrajetResponse findById(Long trajetId);

    Page<TrajetResponse> search(
            TrajetRequest request,
            Pageable pageable
    );

    void finish(Long trajetId, Long conducteurId);

    void cancel(Long trajetId, Long conducteurId);
}