package com.coisee.covoiturage.service.impl;

import com.coisee.covoiturage.dto.pointarret.PointArretRequest;
import com.coisee.covoiturage.dto.trajet.TrajetRequest;
import com.coisee.covoiturage.entity.Conducteur;
import com.coisee.covoiturage.entity.Vehicule;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
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
}
