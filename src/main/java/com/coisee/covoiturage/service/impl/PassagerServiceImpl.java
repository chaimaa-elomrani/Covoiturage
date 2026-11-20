package com.coisee.covoiturage.service.impl;

import com.coisee.covoiturage.service.PassagerService;
import com.coisee.covoiturage.dto.passager.PassagerRequest;
import com.coisee.covoiturage.dto.passager.PassagerResponse;
import com.coisee.covoiturage.entity.Passager;
import com.coisee.covoiturage.exception.BusinessRuleException;
import com.coisee.covoiturage.exception.ResourceNotFoundException;
import com.coisee.covoiturage.mapper.PassagerMapper;
import com.coisee.covoiturage.repository.ConducteurRepository;
import com.coisee.covoiturage.repository.PassagerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PassagerServiceImpl implements PassagerService{

    private static final Logger log =
            LoggerFactory.getLogger(PassagerServiceImpl.class);

    private final PassagerRepository passagerRepository;
    private final ConducteurRepository conducteurRepository;
    private final PassagerMapper passagerMapper;

    public PassagerServiceImpl(PassagerRepository passagerRepository, ConducteurRepository conducteurRepository, PassagerMapper passagerMapper) {
        this.passagerRepository = passagerRepository;
        this.conducteurRepository = conducteurRepository;
        this.passagerMapper = passagerMapper;
    }


    private void verifyEmailDisponible(String email){
        if(passagerRepository.existsByEmailIgnoreCase(email) || conducteurRepository.existsByEmailIgnoreCase(email)){
            log.warn("Création refusée : adresse email déjà utilisée.");
            throw new BusinessRuleException("email is already used");
        }
    }

    @Override
    public PassagerResponse create(PassagerRequest request){
        verifyEmailDisponible(request.email());
        Passager passager = passagerMapper.toEntity(request);
        Passager savedPassager = passagerRepository.save(passager);
        log.info("Passager créé : passagerId={}", savedPassager.getId());
        return passagerMapper.toResponse(savedPassager);
    }

    @Override
    public PassagerResponse findById(Long id){
        return passagerRepository.findById(id)
                .map(passagerMapper::toResponse)
                .orElseThrow(()-> new ResourceNotFoundException("user not found"));
    }

    @Override
    public List<PassagerResponse> findAll(){
        return passagerRepository.findAll()
                .stream()
                .map(passagerMapper::toResponse)
                .toList();
    }

    @Override
    public PassagerResponse findByEmail(String email){
        return passagerRepository.findByEmailIgnoreCase(email)
                .map(passagerMapper::toResponse)
                .orElseThrow(()-> new ResourceNotFoundException("user with this email not found "));
    }

    private void verifyEmailDisponibleUpdate(String email, Long id){
        if(passagerRepository.existsByEmailIgnoreCaseAndIdNot(email, id) || conducteurRepository.existsByEmailIgnoreCase(email)){
            log.warn("Modification refusée : email déjà utilisé pour passagerId={}", id);
            throw new BusinessRuleException("email is already used");
        }
    }


    @Override
    public PassagerResponse update(Long id , PassagerRequest request){
        Passager passager = passagerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("not found"));
        verifyEmailDisponibleUpdate(request.email(), id);
        passagerMapper.updateEntity(request, passager);
        Passager updatedPassager = passagerRepository.save(passager);
        log.info("Profil passager modifié : passagerId={}", id);
        return passagerMapper.toResponse(updatedPassager);
    }
}
