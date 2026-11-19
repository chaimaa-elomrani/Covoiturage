package com.coisee.covoiturage.service;

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
import com.coisee.covoiturage.mapper.PassagerMapper;
import com.coisee.covoiturage.mapper.ReservationMapper;
import com.coisee.covoiturage.repository.PassagerRepository;
import com.coisee.covoiturage.repository.PointArretRepository;
import com.coisee.covoiturage.repository.ReservationRepository;
import com.coisee.covoiturage.repository.TrajetRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PassagerRepository passagerRepository;
    private final TrajetRepository trajetRepository;
    private final PointArretRepository pointArretRepository;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository, PassagerRepository passagerRepository, TrajetRepository trajetRepository, PointArretRepository pointArretRepository, ReservationMapper reservationMapper
    ) {
        this.reservationRepository = reservationRepository;
        this.passagerRepository = passagerRepository;
        this.trajetRepository = trajetRepository;
        this.pointArretRepository = pointArretRepository;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public ReservationResponse reserve(ReservationRequest request) {
        Passager passager = passagerRepository.findById(request.passagerId())
                .orElseThrow(() -> new ResourceNotFoundException("passager not found"));

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

    @Override
    public ReservationResponse findById(Long reservationId, Long passagerId) {
         return reservationRepository.findById(passagerId)
                 .map(reservationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("reservation not found"));

    }

    @Override
    public List<ReservationResponse> findAllByPassagerId(Long passagerId) {
        return reservationRepository.findAllByPassagerIdOrderByDateReservationDesc(passagerId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReservationResponse> findAllByTrajetId(Long trajetId, Long conducteurId) {
        return reservationRepository.findAllByTrajetId(trajetId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public void cancel(Long reservationId, Long passagerId) {

    }


}
