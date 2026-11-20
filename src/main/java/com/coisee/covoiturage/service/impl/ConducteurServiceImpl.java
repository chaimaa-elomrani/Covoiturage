package com.coisee.covoiturage.service.impl;

import com.coisee.covoiturage.service.ConducteurService;

import com.coisee.covoiturage.dto.conducteur.ConducteurRequest;
import com.coisee.covoiturage.dto.conducteur.ConducteurResponse;
import com.coisee.covoiturage.entity.Conducteur;
import com.coisee.covoiturage.exception.BusinessRuleException;
import com.coisee.covoiturage.exception.ResourceNotFoundException;
import com.coisee.covoiturage.mapper.ConducteurMapper;
import com.coisee.covoiturage.repository.ConducteurRepository;
import com.coisee.covoiturage.repository.PassagerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ConducteurServiceImpl implements ConducteurService{

    private static final Logger log =
            LoggerFactory.getLogger(ConducteurServiceImpl.class);

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
            log.warn("Création ou modification refusée : adresse email déjà utilisée.");
            throw new BusinessRuleException("cette adresse email est deja utilisé");
        }
    }

    private void verifyPermisDisponible(String numeroPermis){
        if (conducteurRepository.existsByNumeroPermisIgnoreCase(numeroPermis)) {
            log.warn("Création refusée : numéro de permis déjà utilisé.");
            throw new BusinessRuleException("Ce numéro de permis est déjà utilisé.");
        }
    }
    @Override
    public ConducteurResponse create(ConducteurRequest request){
        verifyEmailDisponible(request.email());
        verifyPermisDisponible(request.numeroPermis());

        Conducteur conducteur = conducteurMapper.toEntity(request);
        Conducteur savedConducteur = conducteurRepository.save(conducteur);
        log.info("Conducteur créé : conducteurId={}", savedConducteur.getId());
        return conducteurMapper.toResponse(savedConducteur);
    }

    @Override
    public ConducteurResponse findById(Long id){
        return conducteurRepository.findById(id).map(conducteurMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur inntrouvable : " + id));
    }

    @Override
    public List<ConducteurResponse>  findAll(){
        return conducteurRepository.findAll()
                .stream().map(conducteurMapper::toResponse)
                .toList();
    }

    @Override
    public List<ConducteurResponse> search(String searchTerm){
        String term = searchTerm.trim() ;
        return conducteurRepository.findByNomContainingIgnoreCaseOrEmailIgnoreCase(term , term)
                .stream()
                .map(conducteurMapper::toResponse)
                .toList();
    }


    private  void verifierEmailDisponiblePourModification(String email , Long id ){
        boolean usedEmailConducteur = conducteurRepository.existsByEmailIgnoreCaseAndIdNot(email, id);
        boolean usedEmailPassager = passagerRepository.existsByEmailIgnoreCase(email);

        if (usedEmailConducteur || usedEmailPassager){
            log.warn("Modification refusée : email déjà utilisé pour conducteurId={}", id);
            throw  new BusinessRuleException("this email is already used");
        }
    }

    private void verifierPermisDisponiblePourModification(String numeroPermis, Long id){
        if (conducteurRepository.existsByNumeroPermisIgnoreCaseAndIdNot(
                numeroPermis,
                id
        )){
            log.warn("Modification refusée : numéro de permis déjà utilisé pour conducteurId={}", id);
            throw  new BusinessRuleException("num permis is already used");
        }
    }
    @Override
    public ConducteurResponse  update(Long id, ConducteurRequest request){
        Conducteur conducteur = conducteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur introuvable : " + id));
        verifierEmailDisponiblePourModification(request.email(), id);
        verifierPermisDisponiblePourModification(request.numeroPermis(), id);
        conducteurMapper.updateEntity(request , conducteur);
        Conducteur updatedConducteur = conducteurRepository.save(conducteur);
        log.info("Profil conducteur modifié : conducteurId={}", id);
        return conducteurMapper.toResponse(updatedConducteur);
    }





}
