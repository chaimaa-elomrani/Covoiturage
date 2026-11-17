package com.coisee.covoiturage.dto.reservation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationRequest(
		@NotNull(message = "L’identifiant du passager est obligatoire.")
		@Positive(message = "L’identifiant du passager doit être positif.")
		Long passagerId,

		@NotNull(message = "L’identifiant du trajet est obligatoire.")
		@Positive(message = "L’identifiant du trajet doit être positif.")
		Long trajetId,

		@NotNull(message = "Le point de montée est obligatoire.")
		@Positive(message = "L’identifiant du point de montée doit être positif.")
		Long pointMonteeId,

		@NotNull(message = "Le point de descente est obligatoire.")
		@Positive(message = "L’identifiant du point de descente doit être positif.")
		Long pointDescenteId,

		@NotNull(message = "Le nombre de places est obligatoire.")
		@Positive(message = "Le nombre de places doit être supérieur à zéro.")
		Integer nombrePlaces
) {
}
