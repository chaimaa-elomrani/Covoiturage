package com.coisee.covoiturage.service.impl;

import com.coisee.covoiturage.service.TrajetService;
import com.coisee.covoiturage.dto.pointarret.PointArretRequest;
import com.coisee.covoiturage.dto.trajet.TrajetRequest;
import com.coisee.covoiturage.dto.trajet.TrajetResponse;
import com.coisee.covoiturage.dto.trajet.TrajetSearchRequest;
import com.coisee.covoiturage.entity.Conducteur;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class TrajetServiceImpl implements TrajetService {

    private static final Logger log =
            LoggerFactory.getLogger(TrajetServiceImpl.class);

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
            log.warn("Publication refusée : parcours avec moins de deux points d’arrêt.");
            throw new BusinessRuleException(
                    "Le parcours doit contenir au moins deux points d’arrêt."
            );
        }

       PointArretRequest depart = points.get(0);

        if (depart.ordre() != 0) {
            log.warn("Publication refusée : le premier point n’a pas l’ordre 0.");
            throw new BusinessRuleException(
                    "Le premier point doit avoir l’ordre 0."
            );
        }

        if (!depart.heurePassage().isAfter(LocalDateTime.now())) {
            log.warn("Publication refusée : heure de départ non future.");
            throw new BusinessRuleException(
                    "L’heure de départ doit être dans le futur."
            );
        }

        if (depart.prixDepuisDepart().signum() != 0) {
            log.warn("Publication refusée : prix du départ différent de zéro.");
            throw new BusinessRuleException(
                    "Le prix du premier point doit être égal à 0."
            );
        }

        Set<String> villes = new HashSet<>();
        for (int i = 0 ; i < points.size(); i++){
            PointArretRequest courant = points.get(i);
            String ville = courant.ville().trim().toLowerCase(Locale.ROOT);

            if (!villes.add(ville)){
                log.warn("Publication refusée : ville répétée dans le parcours.");
                throw new BusinessRuleException(
                        "Une ville ne peut apparaître qu’une seule fois dans le parcours."
                );
            }

            if (i == 0){
                continue;
            }

            PointArretRequest previous = points.get(i - 1);

            if(courant.ordre() != previous.ordre()+ 1){
                log.warn("Publication refusée : ordre des points d’arrêt invalide.");
                throw new BusinessRuleException(
                        "Les ordres des points d’arrêt doivent se suivre."
                );
            }

            if (!courant.heurePassage().isAfter(previous.heurePassage())) {
                log.warn("Publication refusée : heures de passage non croissantes.");
                throw new BusinessRuleException(
                        "Les heures de passage doivent être strictement croissantes."
                );
            }

            if (courant.prixDepuisDepart()
                    .compareTo(previous.prixDepuisDepart()) <= 0) {
                log.warn("Publication refusée : prix des points d’arrêt non croissants.");
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
                .orElseThrow(() -> {
                    log.warn("Publication refusée : véhicule non détenu par le conducteur, conducteurId={}, vehiculeId={}",
                            conducteur.getId(), request.vehiculeId());
                    return new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "Ce véhicule n’appartient pas à ce conducteur."
                    );
                });

        if (request.placesProposees() == null || request.placesProposees() < 1) {
            log.warn("Publication refusée : nombre de places proposé inférieur à 1.");
            throw new BusinessRuleException(
                    "Le trajet doit proposer au moins une place."
            );
        }

        if (request.placesProposees() > vehicule.getCapacite()) {
            log.warn("Publication refusée : places proposées supérieures à la capacité, vehiculeId={}",
                    vehicule.getId());
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
        log.info("Trajet publié : trajetId={}, conducteurId={}, vehiculeId={}",
                savedTrajet.getId(), conducteur.getId(), vehicule.getId());
        return trajetMapper.toResponse(savedTrajet, savedTrajet.getPlacesProposees());

    }




    @Override
    public TrajetResponse findById(Long trajetId) {
        Trajet trajet = trajetRepository.findById(trajetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Trajet introuvable.")
                );
        Long placesReservees =
                reservationRepository.sumNombrePlaceByTrajetIdAndStatus(
                        trajetId,
                        StatutReservation.CONFIRMEE
                );
        int placesRestantes =
                trajet.getPlacesProposees() - placesReservees.intValue();

        return trajetMapper.toResponse(trajet, placesRestantes);
    }




    @Override
    public Page<TrajetResponse> search(TrajetSearchRequest request, Pageable pageable) {
        LocalDateTime debutJournee = request.dateDepart().atStartOfDay();
        LocalDateTime lendemain = debutJournee.plusDays(1);

        Page<Trajet> trajets = trajetRepository.search(
                request.villeDepart().trim(),
                request.villeArrivee().trim(),
                debutJournee,
                lendemain,
                request.nombrePlaces(),
                request.prixMax(),
                StatutTrajet.PUBLIE,
                StatutReservation.CONFIRMEE,
                pageable
        );
        return trajets.map(trajet -> {
                    Long placesReservees =
                            reservationRepository.sumNombrePlaceByTrajetIdAndStatus(
                                    trajet.getId(),
                                    StatutReservation.CONFIRMEE
                            );

                    int placesRestantes =
                            trajet.getPlacesProposees() - placesReservees.intValue();

                    return trajetMapper.toResponse(trajet, placesRestantes);
                });
    }



    @Override
    public void finish(Long trajetId, Long conducteurId) {
        Trajet trajet = trouverTrajetDuConducteur(trajetId , conducteurId);
        verifierTrajetModifiable(trajet);
        if (trajet.getPointsArret().isEmpty()) {
            log.warn("Fin du trajet refusée : aucun point d’arrêt, trajetId={}", trajetId);
            throw new BusinessRuleException(
                    "Le trajet ne contient aucun point d’arrêt."
            );
        }

        LocalDateTime heureDepart = trajet.getPointsArret().get(0).getHeurePassage();

        if (!LocalDateTime.now().isAfter(heureDepart)) {
            log.warn("Fin du trajet refusée avant le départ : trajetId={}, conducteurId={}",
                    trajetId, conducteurId);
            throw new BusinessRuleException(
                    "Le trajet ne peut être terminé qu’après son heure de départ."
            );
        }

        trajet.setStatut(StatutTrajet.TERMINE);
        log.info("Trajet terminé : trajetId={}, conducteurId={}", trajetId, conducteurId);
    }




    private Trajet trouverTrajetDuConducteur(Long trajetId , Long conducteurId){
        Trajet trajet =  trajetRepository.findById(trajetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Trajet introuvable.")
                );

        if(!trajet.getConducteur().getId().equals(conducteurId)){
            log.warn("Accès refusé au trajet : trajetId={}, conducteurId={}",
                    trajetId, conducteurId);
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Ce conducteur n’est pas propriétaire de ce trajet."
            );
        }
        return trajet ;
    }




    private void verifierTrajetModifiable(Trajet trajet) {
        if (trajet.getStatut() != StatutTrajet.PUBLIE
                && trajet.getStatut() != StatutTrajet.COMPLET) {
            log.warn("Modification refusée pour le trajet : trajetId={}, statut={}",
                    trajet.getId(), trajet.getStatut());
            throw new BusinessRuleException(
                    "Seul un trajet publié ou complet peut être terminé ou annulé."
            );
        }
    }



    @Override
    public void cancel(Long trajetId, Long conducteurId) {
        Trajet trajet = trouverTrajetDuConducteur(trajetId, conducteurId);
        verifierTrajetModifiable(trajet);

        List<Reservation> reservations = reservationRepository.findAllByTrajetId(trajetId);
        reservations.stream().filter(reservation ->
                reservation.getStatut() == StatutReservation.CONFIRMEE
        )
                .forEach(reservation -> reservation.setStatut(StatutReservation.ANNULEE));
        trajet.setStatut(StatutTrajet.ANNULE);
        log.info("Trajet annulé avec ses réservations confirmées : trajetId={}, conducteurId={}",
                trajetId, conducteurId);

    }
}
