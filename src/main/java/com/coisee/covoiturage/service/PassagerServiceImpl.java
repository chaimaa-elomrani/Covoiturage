package com.coisee.covoiturage.service;

import com.coisee.covoiturage.mapper.PassagerMapper;
import com.coisee.covoiturage.repository.ConducteurRepository;
import com.coisee.covoiturage.repository.PassagerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PassagerServiceImpl implements PassagerService{

    private final PassagerRepository passagerRepository;
    private final ConducteurRepository conducteurRepository;
    private final PassagerMapper passagerMapper;

    public PassagerServiceImpl(PassagerRepository passagerRepository, ConducteurRepository conducteurRepository, PassagerMapper passagerMapper) {
        this.passagerRepository = passagerRepository;
        this.conducteurRepository = conducteurRepository;
        this.passagerMapper = passagerMapper;
    }


}
