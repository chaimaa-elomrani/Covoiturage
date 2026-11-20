package com.coisee.covoiturage.service.impl;

import com.coisee.covoiturage.dto.reservation.ReservationRequest;
import com.coisee.covoiturage.dto.reservation.ReservationResponse;
import com.coisee.covoiturage.entity.Passager;
import com.coisee.covoiturage.entity.PointArret;
import com.coisee.covoiturage.entity.Reservation;
import com.coisee.covoiturage.entity.Trajet;
import com.coisee.covoiturage.enums.StatutReservation;
import com.coisee.covoiturage.enums.StatutTrajet;
import com.coisee.covoiturage.exception.BusinessRuleException;
import com.coisee.covoiturage.mapper.ReservationMapper;
import com.coisee.covoiturage.repository.ConducteurRepository;
import com.coisee.covoiturage.repository.PassagerRepository;
import com.coisee.covoiturage.repository.PointArretRepository;
import com.coisee.covoiturage.repository.ReservationRepository;
import com.coisee.covoiturage.repository.TrajetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PassagerRepository passagerRepository;

    @Mock
    private ConducteurRepository conducteurRepository;

    @Mock
    private TrajetRepository trajetRepository;

    @Mock
    private PointArretRepository pointArretRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void reserveShouldCalculateSegmentPriceAndCompleteTripWhenLastSeatsAreTaken() {
        Passager passager = mock(Passager.class);
        when(passager.getId()).thenReturn(5L);
        when(passager.getEmail()).thenReturn("passager@example.com");
        when(passagerRepository.findById(5L)).thenReturn(Optional.of(passager));
        when(conducteurRepository.existsByEmailIgnoreCase("passager@example.com"))
                .thenReturn(false);

        Trajet trajet = mock(Trajet.class);
        when(trajet.getId()).thenReturn(20L);
        when(trajet.getStatut()).thenReturn(StatutTrajet.PUBLIE);
        when(trajet.getPlacesProposees()).thenReturn(3);
        when(trajetRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(trajet));
        when(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(
                5L, 20L, StatutReservation.CONFIRMEE
        )).thenReturn(false);

        PointArret montee = mock(PointArret.class);
        PointArret descente = mock(PointArret.class);
        when(montee.getOrdre()).thenReturn(1);
        when(montee.getPrixDepuisDepart()).thenReturn(new BigDecimal("40.00"));
        when(descente.getOrdre()).thenReturn(2);
        when(descente.getPrixDepuisDepart()).thenReturn(new BigDecimal("70.00"));
        when(pointArretRepository.findByIdAndTrajetId(11L, 20L))
                .thenReturn(Optional.of(montee));
        when(pointArretRepository.findByIdAndTrajetId(12L, 20L))
                .thenReturn(Optional.of(descente));
        when(reservationRepository.sumNombrePlaceByTrajetIdAndStatus(
                20L, StatutReservation.CONFIRMEE
        )).thenReturn(1L);

        ReservationRequest request = new ReservationRequest(5L, 20L, 11L, 12L, 2);
        Reservation reservation = mock(Reservation.class);
        ReservationResponse response = new ReservationResponse(
                30L, 5L, 20L, 11L, 12L, 2,
                new BigDecimal("60.00"), LocalDateTime.now(),
                StatutReservation.CONFIRMEE
        );
        when(reservationMapper.toEntity(request)).thenReturn(reservation);
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(reservationMapper.toResponse(reservation)).thenReturn(response);

        ReservationResponse result = reservationService.reserve(request);

        assertThat(result).isEqualTo(response);
        verify(reservation).setMontantTotal(new BigDecimal("60.00"));
        verify(trajet).setStatut(StatutTrajet.COMPLET);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void reserveShouldRejectTripThatIsNotPublished() {
        Passager passager = preparePassager();
        Trajet trajet = mock(Trajet.class);
        when(trajet.getId()).thenReturn(20L);
        when(trajet.getStatut()).thenReturn(StatutTrajet.COMPLET);
        when(trajetRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(trajet));

        assertThatThrownBy(() -> reservationService.reserve(validRequest(1)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("this trajet is not open for reservations");

        verify(reservationRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void reserveShouldRejectSecondConfirmedReservationForSamePassengerAndTrip() {
        Passager passager = preparePassager();
        when(passager.getId()).thenReturn(5L);
        openTrip();
        when(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(
                5L, 20L, StatutReservation.CONFIRMEE
        )).thenReturn(true);

        assertThatThrownBy(() -> reservationService.reserve(validRequest(1)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("this user already have a confirmed reservation for this path");

        verify(reservationRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void reserveShouldRejectBoardingAfterDestination() {
        Passager passager = preparePassager();
        when(passager.getId()).thenReturn(5L);
        openTrip();
        when(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(
                5L, 20L, StatutReservation.CONFIRMEE
        )).thenReturn(false);

        PointArret montee = mock(PointArret.class);
        PointArret descente = mock(PointArret.class);
        when(montee.getOrdre()).thenReturn(2);
        when(descente.getOrdre()).thenReturn(1);
        when(pointArretRepository.findByIdAndTrajetId(11L, 20L))
                .thenReturn(Optional.of(montee));
        when(pointArretRepository.findByIdAndTrajetId(12L, 20L))
                .thenReturn(Optional.of(descente));

        assertThatThrownBy(() -> reservationService.reserve(validRequest(1)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Le point de montée doit précéder le point de descente.");

        verify(reservationRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void reserveShouldRejectWhenNotEnoughSeatsRemain() {
        Passager passager = preparePassager();
        when(passager.getId()).thenReturn(5L);
        Trajet trajet = openTrip();
        when(trajet.getPlacesProposees()).thenReturn(2);
        when(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(
                5L, 20L, StatutReservation.CONFIRMEE
        )).thenReturn(false);
        stubStopsInOrder();
        when(reservationRepository.sumNombrePlaceByTrajetIdAndStatus(
                20L, StatutReservation.CONFIRMEE
        )).thenReturn(2L);

        assertThatThrownBy(() -> reservationService.reserve(validRequest(1)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Il ne reste pas assez de places.");

        verify(reservationRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void reserveShouldRejectDriverAsPassenger() {
        Passager passager = preparePassager();
        when(passager.getId()).thenReturn(5L);
        when(conducteurRepository.existsByEmailIgnoreCase(passager.getEmail()))
                .thenReturn(true);

        assertThatThrownBy(() -> reservationService.reserve(validRequest(1)))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void cancelShouldRejectWhenLessThan24HoursRemain() {
        Passager passager = mock(Passager.class);
        when(passager.getId()).thenReturn(5L);
        Reservation reservation = mock(Reservation.class);
        Trajet trajet = mock(Trajet.class);
        PointArret depart = mock(PointArret.class);
        when(reservationRepository.findById(30L)).thenReturn(Optional.of(reservation));
        when(reservation.getPassager()).thenReturn(passager);
        when(reservation.getTrajet()).thenReturn(trajet);
        when(trajet.getId()).thenReturn(20L);
        when(trajetRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(trajet));
        when(reservation.getStatut()).thenReturn(StatutReservation.CONFIRMEE);
        when(pointArretRepository.findAllByTrajetIdOrderByOrdreAsc(20L))
                .thenReturn(List.of(depart));
        when(depart.getHeurePassage()).thenReturn(LocalDateTime.now().plusHours(2));

        assertThatThrownBy(() -> reservationService.cancel(30L, 5L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Une réservation ne peut être annulée moins de 24 heures avant le départ.");
    }

    @Test
    void cancelShouldCancelReservationAndReopenFullTrip() {
        Passager passager = mock(Passager.class);
        when(passager.getId()).thenReturn(5L);
        Reservation reservation = mock(Reservation.class);
        Trajet trajet = mock(Trajet.class);
        PointArret depart = mock(PointArret.class);
        when(reservationRepository.findById(30L)).thenReturn(Optional.of(reservation));
        when(reservation.getPassager()).thenReturn(passager);
        when(reservation.getTrajet()).thenReturn(trajet);
        when(trajet.getId()).thenReturn(20L);
        when(trajetRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(trajet));
        when(reservation.getStatut()).thenReturn(StatutReservation.CONFIRMEE);
        when(pointArretRepository.findAllByTrajetIdOrderByOrdreAsc(20L))
                .thenReturn(List.of(depart));
        when(depart.getHeurePassage()).thenReturn(LocalDateTime.now().plusDays(2));
        when(trajet.getStatut()).thenReturn(StatutTrajet.COMPLET);

        reservationService.cancel(30L, 5L);

        verify(reservation).setStatut(StatutReservation.ANNULEE);
        verify(trajet).setStatut(StatutTrajet.PUBLIE);
    }

    @Test
    void cancelShouldRejectAnotherPassengersReservation() {
        Passager owner = mock(Passager.class);
        when(owner.getId()).thenReturn(8L);
        Reservation reservation = mock(Reservation.class);
        when(reservationRepository.findById(30L)).thenReturn(Optional.of(reservation));
        when(reservation.getPassager()).thenReturn(owner);

        assertThatThrownBy(() -> reservationService.cancel(30L, 5L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    private Passager preparePassager() {
        Passager passager = mock(Passager.class);
        when(passager.getEmail()).thenReturn("passager@example.com");
        when(passagerRepository.findById(5L)).thenReturn(Optional.of(passager));
        when(conducteurRepository.existsByEmailIgnoreCase("passager@example.com"))
                .thenReturn(false);
        return passager;
    }

    private Trajet openTrip() {
        Trajet trajet = mock(Trajet.class);
        when(trajet.getId()).thenReturn(20L);
        when(trajet.getStatut()).thenReturn(StatutTrajet.PUBLIE);
        when(trajetRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(trajet));
        return trajet;
    }

    private void stubStopsInOrder() {
        PointArret montee = mock(PointArret.class);
        PointArret descente = mock(PointArret.class);
        when(montee.getOrdre()).thenReturn(1);
        when(descente.getOrdre()).thenReturn(2);
        when(pointArretRepository.findByIdAndTrajetId(11L, 20L))
                .thenReturn(Optional.of(montee));
        when(pointArretRepository.findByIdAndTrajetId(12L, 20L))
                .thenReturn(Optional.of(descente));
    }

    private ReservationRequest validRequest(int seats) {
        return new ReservationRequest(5L, 20L, 11L, 12L, seats);
    }
}
