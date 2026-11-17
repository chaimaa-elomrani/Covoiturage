package com.coisee.covoiturage.dto.trajet;

import com.coisee.covoiturage.dto.pointarret.PointArretRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TrajetRequest(
		@NotNull(message = "L’identifiant du conducteur est obligatoire.")
		@Positive(message = "L’identifiant du conducteur doit être positif.")
		Long conducteurId,

		@NotNull(message = "L’identifiant du véhicule est obligatoire.")
		@Positive(message = "L’identifiant du véhicule doit être positif.")
		Long vehiculeId,

		@NotNull(message = "Le nombre de places proposées est obligatoire.")
		@Min(value = 1, message = "Le trajet doit proposer au moins une place.")
		@Max(value = 8, message = "Le trajet ne peut pas proposer plus de 8 places.")
		Integer placesProposees,

		@NotEmpty(message = "Le parcours doit contenir au moins deux points d’arrêt.")
		@Size(min = 2, message = "Le parcours doit contenir au moins deux points d’arrêt.")
		@Valid
		List<PointArretRequest> pointsArret
) {
}
