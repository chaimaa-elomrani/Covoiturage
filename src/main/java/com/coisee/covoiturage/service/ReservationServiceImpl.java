package com.coisee.covoiturage.service;

import com.coisee.covoiturage.dto.reservation.ReservationRequest;
import com.coisee.covoiturage.dto.reservation.ReservationResponse;
import com.coisee.covoiturage.entity.Reservation;
import com.coisee.covoiturage.exception.ResourceNotFoundException;
import com.coisee.covoiturage.mapper.PassagerMapper;
import com.coisee.covoiturage.mapper.ReservationMapper;
import com.coisee.covoiturage.repository.PassagerRepository;
import com.coisee.covoiturage.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PassagerRepository passagerRepository ;
    private final ReservationMapper reservationMapper;


    public ReservationServiceImpl(ReservationRepository reservationRepository, PassagerRepository passagerRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.passagerRepository = passagerRepository;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public ReservationResponse reserve(ReservationRequest request) {
        Reservation reservation = reservationMapper.toEntity(request);
        return reservationMapper.toResponse(reservationRepository.save(reservation));

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
