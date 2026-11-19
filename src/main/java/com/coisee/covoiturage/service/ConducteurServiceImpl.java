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

import java.util.List;

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
        boolean usedEmailConducteur = conducteurRepository.existsByEmailIgnoreCase(email);
        boolean usedEmailPassager = passagerRepository.existsByEmailIgnoreCase(email);

        if (usedEmailConducteur || usedEmailPassager){
            throw  new BusinessRuleException("this email is already used");
        }
    }

    private void verifierPermisDisponiblePourModification(String numroPermis , Long id){
        if (conducteurRepository.existsByNumeroPermisIgnoreCaseAndIdNot(
                numroPermis,
                id
        )){
            throw  new BusinessRuleException("num permis is already used");
        }
    }
    @Override
    public ConducteurResponse  update(Long id, ConducteurRequest request){
        Conducteur conducteur = conducteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        conducteurMapper.updateEntity(request , conducteur);
        return conducteurMapper.toResponse(conducteurRepository.save(conducteur));
    }





}
