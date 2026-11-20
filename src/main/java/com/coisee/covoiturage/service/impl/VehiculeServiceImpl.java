package com.coisee.covoiturage.service.impl;

import com.coisee.covoiturage.service.VehiculeService;
import com.coisee.covoiturage.dto.vehicule.VehiculeRequest;
import com.coisee.covoiturage.dto.vehicule.VehiculeResponse;
import com.coisee.covoiturage.entity.Conducteur;
import com.coisee.covoiturage.entity.Vehicule;
import com.coisee.covoiturage.exception.BusinessRuleException;
import com.coisee.covoiturage.exception.ResourceNotFoundException;
import com.coisee.covoiturage.mapper.VehiculeMapper;
import com.coisee.covoiturage.repository.ConducteurRepository;
import com.coisee.covoiturage.repository.TrajetRepository;
import com.coisee.covoiturage.repository.VehiculeRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class VehiculeServiceImpl implements VehiculeService {

	private final VehiculeRepository vehiculeRepository;
	private final ConducteurRepository conducteurRepository;
	private final TrajetRepository trajetRepository;
	private final VehiculeMapper vehiculeMapper;

	public VehiculeServiceImpl(
			VehiculeRepository vehiculeRepository,
			ConducteurRepository conducteurRepository,
			TrajetRepository trajetRepository,
			VehiculeMapper vehiculeMapper
	) {
		this.vehiculeRepository = vehiculeRepository;
		this.conducteurRepository = conducteurRepository;
		this.trajetRepository = trajetRepository;
		this.vehiculeMapper = vehiculeMapper;
	}

	private void verifierImmatriculationDisponible(String immatriculation) {
		if (vehiculeRepository.existsByImmatriculationIgnoreCase(immatriculation)) {
			throw new BusinessRuleException("Cette immatriculation est déjà utilisée.");
		}
	}

	@Override
	public VehiculeResponse create(VehiculeRequest request) {
		Conducteur conducteur = conducteurRepository.findById(request.conducteurId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Conducteur introuvable : " + request.conducteurId()
				));

		verifierImmatriculationDisponible(request.immatriculation());

		Vehicule vehicule = vehiculeMapper.toEntity(request);
		vehicule.setConducteur(conducteur);
		Vehicule savedVehicule = vehiculeRepository.save(vehicule);
		return vehiculeMapper.toResponse(savedVehicule);
	}


	private Vehicule trouverVehiculeEtVerifierProprietaire(Long vehiculeId, Long conducteurId) {
		Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
				.orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable : " + vehiculeId));

		if (!vehicule.getConducteur().getId().equals(conducteurId)) {
			throw new ResponseStatusException(
					HttpStatus.FORBIDDEN,
					"Ce véhicule appartient à un autre conducteur."
			);
		}

		return vehicule;
	}

	@Override
	public VehiculeResponse findById(Long vehiculeId, Long conducteurId) {
		return vehiculeMapper.toResponse(trouverVehiculeEtVerifierProprietaire(vehiculeId, conducteurId));
	}

	@Override
	public List<VehiculeResponse> findAllByConducteurId(Long conducteurId) {
		if (!conducteurRepository.existsById(conducteurId)) {
			throw new ResourceNotFoundException("Conducteur introuvable : " + conducteurId);
		}

		return vehiculeRepository.findAllByConducteurId(conducteurId)
				.stream()
				.map(vehiculeMapper::toResponse)
				.toList();
	}

	private void verifierImmatriculationDisponiblePourModification(
			String immatriculation,
			Long vehiculeId
	) {
		if (vehiculeRepository.existsByImmatriculationIgnoreCaseAndIdNot(
				immatriculation,
				vehiculeId
		)) {
			throw new BusinessRuleException("Cette immatriculation est déjà utilisée.");
		}
	}

	@Override
	public VehiculeResponse update(Long vehiculeId, Long conducteurId, VehiculeRequest request) {
		Vehicule vehicule = trouverVehiculeEtVerifierProprietaire(vehiculeId, conducteurId);

		verifierImmatriculationDisponiblePourModification(
				request.immatriculation(),
				vehiculeId
		);

		vehiculeMapper.updateEntity(request, vehicule);
		Vehicule savedVehicule = vehiculeRepository.save(vehicule);
		return vehiculeMapper.toResponse(savedVehicule);
	}



	@Override
	public void supprimer(Long vehiculeId, Long conducteurId) {
		Vehicule vehicule = trouverVehiculeEtVerifierProprietaire(vehiculeId, conducteurId);

		if (trajetRepository.existsByVehiculeId(vehiculeId)) {
			throw new BusinessRuleException(
					"Ce véhicule ne peut pas être supprimé car il est associé à un trajet."
			);
		}

		vehiculeRepository.delete(vehicule);
	}

}
