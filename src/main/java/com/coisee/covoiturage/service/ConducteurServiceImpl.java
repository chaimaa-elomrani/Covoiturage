package com.coisee.covoiturage.service;


import com.coisee.covoiturage.dto.conducteur.ConducteurRequest;
import com.coisee.covoiturage.dto.conducteur.ConducteurResponse;
import com.coisee.covoiturage.exception.BusinessRuleException;
import com.coisee.covoiturage.mapper.ConducteurMapper;
import com.coisee.covoiturage.repository.ConducteurRepository;
import com.coisee.covoiturage.repository.PassagerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ConducteurServiceImpl implements ConducteurService{

    private final ConducteurRepository conducteurRepository ;
    private final PassagerRepository passagerRepository;
    private final ConducteurMapper conducteurMapper;


    public ConducteurServiceImpl(ConducteurRepository conducteurRepository, PassagerRepository passagerRepository, ConducteurMapper conducteurMapper) {
        this.conducteurRepository = conducteurRepository;
        this.passagerRepository = passagerRepository;
        this.conducteurMapper = conducteurMapper;
    }

    private void verifyEmailDisponible(String email){
        if(conducteurRepository.existsByEmailIgnoreCase(email) || passagerRepository.existsByEmailIgnoreCase(email)){
            throw new BusinessRuleException("cette adresse email est deja utilisé");
        }
    }

    private void verifierPermisDisponible(String numPermis){
        if (conducteurRepository.existsByNumeroPermisIgnoreCase(numPermis)) {
            throw new BusinessRuleException("Ce numéro de permis est déjà utilisé.");
        }
    }


}
