package com.coisee.covoiturage.service;


import com.coisee.covoiturage.dto.conducteur.ConducteurRequest;
import com.coisee.covoiturage.dto.conducteur.ConducteurResponse;
import com.coisee.covoiturage.entity.Conducteur;
import com.coisee.covoiturage.exception.BusinessRuleException;
import com.coisee.covoiturage.exception.ResourceNotFoundException;
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

    private void verifyPermisDisponible(String numeroPermis){
        if (conducteurRepository.existsByNumeroPermisIgnoreCase(numeroPermis)) {
            throw new BusinessRuleException("Ce numéro de permis est déjà utilisé.");
        }
    }
    @Override
    public ConducteurResponse create(ConducteurRequest request){
        verifyEmailDisponible(request.email());
        verifyPermisDisponible(request.numeroPermis());

        Conducteur conducteur = conducteurMapper.toEntity(request);
        return conducteurMapper.toResponse(conducteurRepository.save(conducteur));
    }

    @Override
    public ConducteurResponse findById(Long id){
        return conducteurRepository.findById(id).map(conducteurMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur inntrouvable : " + id));
    }

}
