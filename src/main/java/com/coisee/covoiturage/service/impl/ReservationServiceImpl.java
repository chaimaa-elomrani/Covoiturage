package com.coisee.covoiturage.service.impl;

import com.coisee.covoiturage.service.ReservationService;
import com.coisee.covoiturage.dto.reservation.ReservationRequest;
import com.coisee.covoiturage.dto.reservation.ReservationResponse;
import com.coisee.covoiturage.entity.Passager;
import com.coisee.covoiturage.entity.PointArret;
import com.coisee.covoiturage.entity.Reservation;
import com.coisee.covoiturage.entity.Trajet;
import com.coisee.covoiturage.enums.StatutReservation;
import com.coisee.covoiturage.enums.StatutTrajet;
import com.coisee.covoiturage.exception.BusinessRuleException;
import com.coisee.covoiturage.exception.ResourceNotFoundException;
import com.coisee.covoiturage.mapper.ReservationMapper;
import com.coisee.covoiturage.repository.ConducteurRepository;
import com.coisee.covoiturage.repository.PassagerRepository;
import com.coisee.covoiturage.repository.PointArretRepository;
import com.coisee.covoiturage.repository.ReservationRepository;
import com.coisee.covoiturage.repository.TrajetRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PassagerRepository passagerRepository;
    private final ConducteurRepository conducteurRepository;
    private final TrajetRepository trajetRepository;
    private final PointArretRepository pointArretRepository;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository, PassagerRepository passagerRepository, ConducteurRepository conducteurRepository, TrajetRepository trajetRepository, PointArretRepository pointArretRepository, ReservationMapper reservationMapper
    ) {
        this.reservationRepository = reservationRepository;
        this.passagerRepository = passagerRepository;
        this.conducteurRepository = conducteurRepository;
        this.trajetRepository = trajetRepository;
        this.pointArretRepository = pointArretRepository;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public ReservationResponse reserve(ReservationRequest request) {
        Passager passager = passagerRepository.findById(request.passagerId())
                .orElseThrow(() -> new ResourceNotFoundException("passager not found"));

        if (conducteurRepository.existsByEmailIgnoreCase(passager.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un conducteur ne peut pas effectuer de réservation."
            );
        }

        Trajet trajet = trajetRepository.findByIdForUpdate(request.trajetId())
                .orElseThrow(() -> new ResourceNotFoundException("trajet not found"));

        if(trajet.getStatut() != StatutTrajet.PUBLIE){
            throw new BusinessRuleException("this trajet is not open for reservations");
        }

        if (reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(passager.getId() , trajet.getId() , StatutReservation.CONFIRMEE))
        {
            throw new BusinessRuleException("this user already have a confirmed reservation for this path");
        }

        PointArret pointMontee = pointArretRepository.findByIdAndTrajetId(request.pointMonteeId() , trajet.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Le point de montée n’appartient pas à ce trajet."
                ));
        PointArret pointDescente = pointArretRepository
                .findByIdAndTrajetId(request.pointDescenteId(), trajet.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Le point de descente n’appartient pas à ce trajet."
                ));

        if (pointMontee.getOrdre() >= pointDescente.getOrdre()){
            throw new BusinessRuleException(
                    "Le point de montée doit précéder le point de descente."
            );
        }

        Long placeReservees = reservationRepository.sumNombrePlaceByTrajetIdAndStatus(trajet.getId() , StatutReservation.CONFIRMEE);
        Long placesRestantes = trajet.getPlacesProposees() - placeReservees ;

        if (request.nombrePlaces() > placesRestantes){
            throw new BusinessRuleException("Il ne reste pas assez de places.");
        }

        BigDecimal prixParPlace = pointDescente.getPrixDepuisDepart()
                .subtract(pointMontee.getPrixDepuisDepart());
        BigDecimal montantTotal = prixParPlace
                .multiply(BigDecimal.valueOf(request.nombrePlaces()))
                .setScale(2, RoundingMode.HALF_UP);

        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setPassager(passager);
        reservation.setTrajet(trajet);
        reservation.setPointMontee(pointMontee);
        reservation.setPointDescente(pointDescente);
        reservation.setMontantTotal(montantTotal);

        Reservation res = reservationRepository.save(reservation);
        if (placesRestantes - request.nombrePlaces() == 0){
            trajet.setStatut(StatutTrajet.COMPLET);
        }
        return reservationMapper.toResponse(res);

    }


    private void verifierProprietaire(Reservation reservation , Long passagerId){
        if (!reservation.getPassager().getId().equals(passagerId)){
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Cette réservation appartient à un autre passager."
            );
        }
    }

    @Override
    public ReservationResponse findById(Long reservationId, Long passagerId) {
         Reservation reservation =  reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("reservation not found"));

         verifierProprietaire(reservation , passagerId);
         return reservationMapper.toResponse(reservation);
    }

    @Override
    public List<ReservationResponse> findAllByPassagerId(Long passagerId) {
        if (!passagerRepository.existsById(passagerId)) {
            throw new ResourceNotFoundException("Passager introuvable.");
        }
        return reservationRepository.findAllByPassagerIdOrderByDateReservationDesc(passagerId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReservationResponse> findAllByTrajetId(Long trajetId, Long conducteurId) {
		Trajet trajet = trajetRepository.findById(trajetId)
				.orElseThrow(() -> new ResourceNotFoundException("trajet not found"));

		if (!trajet.getConducteur().getId().equals(conducteurId)) {
			throw new ResponseStatusException(
					HttpStatus.FORBIDDEN,
					"Ce conducteur n’est pas propriétaire de ce trajet."
			);
		}

        return reservationRepository.findAllByTrajetId(trajetId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public void cancel(Long reservationId, Long passagerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable."));
        verifierProprietaire(reservation , passagerId);
        Trajet trajet = trajetRepository.findByIdForUpdate(reservation.getTrajet().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trajet introuvable."));
        if (reservation.getStatut() != StatutReservation.CONFIRMEE){
            throw new BusinessRuleException("Cette réservation n’est pas confirmée.");
        }

        List<PointArret> points = pointArretRepository.findAllByTrajetIdOrderByOrdreAsc(trajet.getId());
        LocalDateTime depart = points.get(0).getHeurePassage();
        if (depart.isBefore(LocalDateTime.now().plusHours(24))){
            throw new BusinessRuleException(
                    "Une réservation ne peut être annulée moins de 24 heures avant le départ."
            );
        }
        reservation.setStatut(StatutReservation.ANNULEE);

        if (trajet.getStatut() == StatutTrajet.COMPLET){
            trajet.setStatut(StatutTrajet.PUBLIE);
        }

    }


}
