package com.coisee.covoiturage.service.impl;

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
import com.coisee.covoiturage.mapper.TrajetMapper;
import com.coisee.covoiturage.repository.ConducteurRepository;
import com.coisee.covoiturage.repository.ReservationRepository;
import com.coisee.covoiturage.repository.TrajetRepository;
import com.coisee.covoiturage.repository.VehiculeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrajetServiceImplTest {

    @Mock
    private TrajetRepository trajetRepository;

    @Mock
    private ConducteurRepository conducteurRepository;

    @Mock
    private VehiculeRepository vehiculeRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TrajetMapper trajetMapper;

    @InjectMocks
    private TrajetServiceImpl trajetService;

    @Test
    void publishShouldRejectTripWhenDepartureIsInThePast() {
        Conducteur conducteur = mock(Conducteur.class);
        when(conducteur.getId()).thenReturn(7L);
        when(conducteurRepository.findById(7L))
                .thenReturn(Optional.of(conducteur));

        Vehicule vehicule = mock(Vehicule.class);
        when(vehicule.getCapacite()).thenReturn(4);
        when(vehiculeRepository.findByIdAndConducteurId(2L, 7L))
                .thenReturn(Optional.of(vehicule));

        LocalDateTime departPasse = LocalDateTime.now().minusHours(2);

        TrajetRequest request = new TrajetRequest(
                7L,
                2L,
                1,
                List.of(
                        new PointArretRequest(
                                "Rabat",
                                0,
                                departPasse,
                                BigDecimal.ZERO
                        ),
                        new PointArretRequest(
                                "Casablanca",
                                1,
                                departPasse.plusHours(1),
                                new BigDecimal("40.00")
                        )
                )
        );

        assertThatThrownBy(() -> trajetService.publish(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("L’heure de départ doit être dans le futur.");
    }

    @Test
    void publishShouldSaveTripAndReturnAllSeatsAsAvailable() {
        Conducteur conducteur = mock(Conducteur.class);
        when(conducteur.getId()).thenReturn(7L);
        when(conducteurRepository.findById(7L))
                .thenReturn(Optional.of(conducteur));

        Vehicule vehicule = mock(Vehicule.class);
        when(vehicule.getCapacite()).thenReturn(4);
        when(vehiculeRepository.findByIdAndConducteurId(2L, 7L))
                .thenReturn(Optional.of(vehicule));

        LocalDateTime depart = LocalDateTime.now().plusDays(1);

        TrajetRequest request = new TrajetRequest(
                7L,
                2L,
                3,
                List.of(
                        new PointArretRequest(
                                "Rabat", 0, depart, BigDecimal.ZERO
                        ),
                        new PointArretRequest(
                                "Casablanca", 1, depart.plusHours(1),
                                new BigDecimal("40.00")
                        )
                )
        );

        Trajet trajet = mock(Trajet.class);
        PointArret pointArret = mock(PointArret.class);

        when(trajet.getPointsArret()).thenReturn(List.of(pointArret));
        when(trajetMapper.toEntity(request)).thenReturn(trajet);
        when(trajetRepository.save(trajet)).thenReturn(trajet);
        when(trajet.getPlacesProposees()).thenReturn(3);

        TrajetResponse expectedResponse = new TrajetResponse(
                10L,
                7L,
                2L,
                3,
                3,
                StatutTrajet.PUBLIE,
                List.of()
        );

        when(trajetMapper.toResponse(trajet, 3))
                .thenReturn(expectedResponse);

        TrajetResponse actualResponse = trajetService.publish(request);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(trajetRepository).save(trajet);
        verify(trajet).setConducteur(conducteur);
        verify(trajet).setVehicule(vehicule);
        verify(trajet).setStatut(StatutTrajet.PUBLIE);
        verify(pointArret).setTrajet(trajet);
    }

    @Test
    void publishShouldRejectMoreSeatsThanVehicleCapacity() {
        Conducteur conducteur = mock(Conducteur.class);
        when(conducteur.getId()).thenReturn(7L);
        when(conducteurRepository.findById(7L))
                .thenReturn(Optional.of(conducteur));

        Vehicule vehicule = mock(Vehicule.class);
        when(vehicule.getCapacite()).thenReturn(4);
        when(vehiculeRepository.findByIdAndConducteurId(2L, 7L))
                .thenReturn(Optional.of(vehicule));

        LocalDateTime depart = LocalDateTime.now().plusDays(1);

        TrajetRequest request = new TrajetRequest(
                7L,
                2L,
                5,
                List.of(
                        new PointArretRequest(
                                "Rabat", 0, depart, BigDecimal.ZERO
                        ),
                        new PointArretRequest(
                                "Casablanca", 1, depart.plusHours(1),
                                new BigDecimal("40.00")
                        )
                )
        );

        assertThatThrownBy(() -> trajetService.publish(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(
                        "Le nombre de places proposées dépasse la capacité du véhicule."
                );

        verifyNoInteractions(trajetRepository);
    }

    @Test
    void publishShouldRejectStopsWhenPassageTimesAreNotIncreasing() {
        Conducteur conducteur = mock(Conducteur.class);
        when(conducteur.getId()).thenReturn(7L);
        when(conducteurRepository.findById(7L))
                .thenReturn(Optional.of(conducteur));

        Vehicule vehicule = mock(Vehicule.class);
        when(vehicule.getCapacite()).thenReturn(4);
        when(vehiculeRepository.findByIdAndConducteurId(2L, 7L))
                .thenReturn(Optional.of(vehicule));

        LocalDateTime depart = LocalDateTime.now().plusDays(1);

        TrajetRequest request = new TrajetRequest(
                7L,
                2L,
                2,
                List.of(
                        new PointArretRequest(
                                "Rabat", 0, depart, BigDecimal.ZERO
                        ),
                        new PointArretRequest(
                                "Casablanca", 1, depart.minusMinutes(30),
                                new BigDecimal("40.00")
                        )
                )
        );

        assertThatThrownBy(() -> trajetService.publish(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(
                        "Les heures de passage doivent être strictement croissantes."
                );

        verifyNoInteractions(trajetRepository);
    }

    @Test
    void publishShouldRejectDuplicateCitiesIgnoringCaseAndSpaces() {
        Conducteur conducteur = mock(Conducteur.class);
        when(conducteur.getId()).thenReturn(7L);
        when(conducteurRepository.findById(7L))
                .thenReturn(Optional.of(conducteur));

        Vehicule vehicule = mock(Vehicule.class);
        when(vehicule.getCapacite()).thenReturn(4);
        when(vehiculeRepository.findByIdAndConducteurId(2L, 7L))
                .thenReturn(Optional.of(vehicule));

        LocalDateTime depart = LocalDateTime.now().plusDays(1);

        TrajetRequest request = new TrajetRequest(
                7L,
                2L,
                2,
                List.of(
                        new PointArretRequest(
                                "Rabat", 0, depart, BigDecimal.ZERO
                        ),
                        new PointArretRequest(
                                " rabat ", 1, depart.plusHours(1),
                                new BigDecimal("40.00")
                        )
                )
        );

        assertThatThrownBy(() -> trajetService.publish(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(
                        "Une ville ne peut apparaître qu’une seule fois dans le parcours."
                );

        verifyNoInteractions(trajetRepository);
    }

    @Test
    void publishShouldRejectVehicleOwnedByAnotherDriver() {
        Conducteur conducteur = mock(Conducteur.class);
        when(conducteur.getId()).thenReturn(7L);
        when(conducteurRepository.findById(7L))
                .thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findByIdAndConducteurId(2L, 7L))
                .thenReturn(Optional.empty());

        TrajetRequest request = validRequest(2);

        assertThatThrownBy(() -> trajetService.publish(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ce véhicule n’appartient pas à ce conducteur.");

        verifyNoInteractions(trajetRepository);
    }

    @Test
    void publishShouldRejectNonZeroDeparturePrice() {
        stubDriverAndVehicle(4);
        LocalDateTime depart = LocalDateTime.now().plusDays(1);
        TrajetRequest request = new TrajetRequest(
                7L, 2L, 2,
                List.of(
                        new PointArretRequest("Rabat", 0, depart, new BigDecimal("1.00")),
                        new PointArretRequest("Casa", 1, depart.plusHours(1), new BigDecimal("40.00"))
                )
        );

        assertThatThrownBy(() -> trajetService.publish(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Le prix du premier point doit être égal à 0.");

        verifyNoInteractions(trajetRepository);
    }

    @Test
    void publishShouldRejectPricesThatDoNotIncrease() {
        stubDriverAndVehicle(4);
        LocalDateTime depart = LocalDateTime.now().plusDays(1);
        TrajetRequest request = new TrajetRequest(
                7L, 2L, 2,
                List.of(
                        new PointArretRequest("Rabat", 0, depart, BigDecimal.ZERO),
                        new PointArretRequest("Casa", 1, depart.plusHours(1), BigDecimal.ZERO)
                )
        );

        assertThatThrownBy(() -> trajetService.publish(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Les prix depuis le départ doivent être strictement croissants.");

        verifyNoInteractions(trajetRepository);
    }

    @Test
    void findByIdShouldReturnTripWithCalculatedRemainingSeats() {
        Trajet trajet = mock(Trajet.class);
        TrajetResponse response = new TrajetResponse(
                10L, 7L, 2L, 4, 2, StatutTrajet.PUBLIE, List.of()
        );
        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));
        when(trajet.getPlacesProposees()).thenReturn(4);
        when(reservationRepository.sumNombrePlaceByTrajetIdAndStatus(
                10L, StatutReservation.CONFIRMEE
        )).thenReturn(2L);
        when(trajetMapper.toResponse(trajet, 2)).thenReturn(response);

        TrajetResponse result = trajetService.findById(10L);

        assertThat(result).isEqualTo(response);
        verify(trajetMapper).toResponse(trajet, 2);
    }

    @Test
    void searchShouldReturnPageWithRemainingSeatsCalculated() {
        Trajet trajet = mock(Trajet.class);
        TrajetResponse response = new TrajetResponse(
                10L, 7L, 2L, 4, 3, StatutTrajet.PUBLIE, List.of()
        );
        LocalDate date = LocalDate.of(2027, 1, 15);
        TrajetSearchRequest request = new TrajetSearchRequest(
                "Rabat", "Casa", date, 1, new BigDecimal("50.00")
        );
        Pageable pageable = Pageable.unpaged();

        when(trajetRepository.search(
                "Rabat", "Casa", date.atStartOfDay(), date.plusDays(1).atStartOfDay(),
                1, new BigDecimal("50.00"), StatutTrajet.PUBLIE,
                StatutReservation.CONFIRMEE, pageable
        )).thenReturn(new PageImpl<>(List.of(trajet)));
        when(trajet.getId()).thenReturn(10L);
        when(trajet.getPlacesProposees()).thenReturn(4);
        when(reservationRepository.sumNombrePlaceByTrajetIdAndStatus(
                10L, StatutReservation.CONFIRMEE
        )).thenReturn(1L);
        when(trajetMapper.toResponse(trajet, 3)).thenReturn(response);

        var result = trajetService.search(request, pageable);

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    void finishShouldRejectTripBeforeDeparture() {
        Trajet trajet = ownedTrip(StatutTrajet.PUBLIE);
        PointArret depart = mock(PointArret.class);
        when(depart.getHeurePassage()).thenReturn(LocalDateTime.now().plusHours(2));
        when(trajet.getPointsArret()).thenReturn(List.of(depart));

        assertThatThrownBy(() -> trajetService.finish(10L, 7L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Le trajet ne peut être terminé qu’après son heure de départ.");
    }

    @Test
    void finishShouldSetStatusToFinishedAfterDeparture() {
        Trajet trajet = ownedTrip(StatutTrajet.PUBLIE);
        PointArret depart = mock(PointArret.class);
        when(depart.getHeurePassage()).thenReturn(LocalDateTime.now().minusHours(1));
        when(trajet.getPointsArret()).thenReturn(List.of(depart));

        trajetService.finish(10L, 7L);

        verify(trajet).setStatut(StatutTrajet.TERMINE);
    }

    @Test
    void cancelShouldCancelConfirmedReservationsAndTrip() {
        Trajet trajet = ownedTrip(StatutTrajet.PUBLIE);
        Reservation confirmed = mock(Reservation.class);
        Reservation cancelled = mock(Reservation.class);
        when(confirmed.getStatut()).thenReturn(StatutReservation.CONFIRMEE);
        when(cancelled.getStatut()).thenReturn(StatutReservation.ANNULEE);
        when(reservationRepository.findAllByTrajetId(10L))
                .thenReturn(List.of(confirmed, cancelled));

        trajetService.cancel(10L, 7L);

        verify(confirmed).setStatut(StatutReservation.ANNULEE);
        verify(cancelled, never()).setStatut(StatutReservation.ANNULEE);
        verify(trajet).setStatut(StatutTrajet.ANNULE);
    }

    private void stubDriverAndVehicle(int capacity) {
        Conducteur conducteur = mock(Conducteur.class);
        when(conducteur.getId()).thenReturn(7L);
        when(conducteurRepository.findById(7L))
                .thenReturn(Optional.of(conducteur));

        Vehicule vehicule = mock(Vehicule.class);
        when(vehicule.getCapacite()).thenReturn(capacity);
        when(vehiculeRepository.findByIdAndConducteurId(2L, 7L))
                .thenReturn(Optional.of(vehicule));
    }

    private TrajetRequest validRequest(int seats) {
        LocalDateTime depart = LocalDateTime.now().plusDays(1);
        return new TrajetRequest(
                7L, 2L, seats,
                List.of(
                        new PointArretRequest("Rabat", 0, depart, BigDecimal.ZERO),
                        new PointArretRequest("Casa", 1, depart.plusHours(1), new BigDecimal("40.00"))
                )
        );
    }

    private Trajet ownedTrip(StatutTrajet status) {
        Trajet trajet = mock(Trajet.class);
        Conducteur conducteur = mock(Conducteur.class);
        when(conducteur.getId()).thenReturn(7L);
        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));
        when(trajet.getConducteur()).thenReturn(conducteur);
        when(trajet.getStatut()).thenReturn(status);
        return trajet;
    }
}
