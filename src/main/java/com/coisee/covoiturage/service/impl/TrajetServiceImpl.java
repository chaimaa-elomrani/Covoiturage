package com.coisee.covoiturage.service.impl;

import com.coisee.covoiturage.service.TrajetService;
import com.coisee.covoiturage.dto.pointarret.PointArretRequest;
import com.coisee.covoiturage.dto.trajet.TrajetRequest;
import com.coisee.covoiturage.dto.trajet.TrajetResponse;
import com.coisee.covoiturage.dto.trajet.TrajetSearchRequest;
import com.coisee.covoiturage.entity.Conducteur;
import com.coisee.covoiturage.entity.PointArret;
import com.coisee.covoiturage.entity.Reservation;
import com.coisee.covoiturage.entity.Trajet;
import com.coisee.covoiturage.entity.Vehicule;
import com.coisee.covoiturage.enums.StatutReservation;
import com.coisee.covoiturage.enums.StatutTrajet;
import com.coisee.covoiturage.exception.BusinessRuleException;
import com.coisee.covoiturage.exception.ResourceNotFoundException;
import com.coisee.covoiturage.mapper.TrajetMapper;
import com.coisee.covoiturage.repository.ConducteurRepository;
import com.coisee.covoiturage.repository.ReservationRepository;
import com.coisee.covoiturage.repository.TrajetRepository;
import com.coisee.covoiturage.repository.VehiculeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class TrajetServiceImpl implements TrajetService {

    private final TrajetRepository trajetRepository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ReservationRepository reservationRepository;
    private final TrajetMapper trajetMapper;

    public TrajetServiceImpl(
            TrajetRepository trajetRepository,
            ConducteurRepository conducteurRepository,
            VehiculeRepository vehiculeRepository,
            ReservationRepository reservationRepository,
            TrajetMapper trajetMapper
    ) {
        this.trajetRepository = trajetRepository;
        this.conducteurRepository = conducteurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.reservationRepository = reservationRepository;
        this.trajetMapper = trajetMapper;
    }


    private void verifierParcours(List<PointArretRequest> points){
        if( points == null || points.size() < 2){
            throw new BusinessRuleException(
                    "Le parcours doit contenir au moins deux points d’arrêt."
            );
        }

       PointArretRequest depart = points.get(0);

        if (depart.ordre() != 0) {
            throw new BusinessRuleException(
                    "Le premier point doit avoir l’ordre 0."
            );
        }

        if (!depart.heurePassage().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException(
                    "L’heure de départ doit être dans le futur."
            );
        }

        if (depart.prixDepuisDepart().signum() != 0) {
            throw new BusinessRuleException(
                    "Le prix du premier point doit être égal à 0."
            );
        }

        Set<String> villes = new HashSet<>();
        for (int i = 0 ; i < points.size(); i++){
            PointArretRequest courant = points.get(i);
            String ville = courant.ville().trim().toLowerCase(Locale.ROOT);

            if (!villes.add(ville)){
                throw new BusinessRuleException(
                        "Une ville ne peut apparaître qu’une seule fois dans le parcours."
                );
            }

            if (i == 0){
                continue;
            }

            PointArretRequest previous = points.get(i - 1);

            if(courant.ordre() != previous.ordre()+ 1){
                throw new BusinessRuleException(
                        "Les ordres des points d’arrêt doivent se suivre."
                );
            }

            if (!courant.heurePassage().isAfter(previous.heurePassage())) {
                throw new BusinessRuleException(
                        "Les heures de passage doivent être strictement croissantes."
                );
            }

            if (courant.prixDepuisDepart()
                    .compareTo(previous.prixDepuisDepart()) <= 0) {
                throw new BusinessRuleException(
                        "Les prix depuis le départ doivent être strictement croissants."
                );
            }
        }

    }

    @Override
    public TrajetResponse publish(TrajetRequest request) {
        Conducteur conducteur = conducteurRepository.findById(request.conducteurId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Conducteur introuvable.")
                );
        Vehicule vehicule = vehiculeRepository
                .findByIdAndConducteurId(request.vehiculeId(), conducteur.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Ce véhicule n’appartient pas à ce conducteur."
                ));

        if (request.placesProposees() > vehicule.getCapacite()) {
            throw new BusinessRuleException(
                    "Le nombre de places proposées dépasse la capacité du véhicule."
            );
        }

        verifierParcours(request.pointsArret());

        Trajet trajet =trajetMapper.toEntity(request);
        trajet.setConducteur(conducteur);
        trajet.setVehicule(vehicule);
        trajet.setStatut(StatutTrajet.PUBLIE);

        trajet.getPointsArret().forEach(point -> point.setTrajet(trajet));
        Trajet savedTrajet = trajetRepository.save(trajet);
        return trajetMapper.toResponse(savedTrajet, savedTrajet.getPlacesProposees());

    }

    @Override
    public TrajetResponse findById(Long trajetId) {
        return null;
    }

    @Override
    public Page<TrajetResponse> search(TrajetSearchRequest request, Pageable pageable) {
        return null;
    }

    @Override
    public void finish(Long trajetId, Long conducteurId) {

    }

    @Override
    public void cancel(Long trajetId, Long conducteurId) {

    }
}
