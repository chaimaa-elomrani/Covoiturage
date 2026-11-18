package com.coisee.covoiturage.service;

import com.coisee.covoiturage.dto.conducteur.ConducteurRequest;
import com.coisee.covoiturage.dto.conducteur.ConducteurResponse;

import java.util.List;

public interface ConducteurService {

    ConducteurResponse create(ConducteurRequest request);

    ConducteurResponse findById(Long id);

    List<ConducteurResponse> findAll();

    List<ConducteurResponse> search(String searchTerm);
}
