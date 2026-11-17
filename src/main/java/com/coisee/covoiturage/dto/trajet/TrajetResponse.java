package com.coisee.covoiturage.dto.trajet;

import com.coisee.covoiturage.dto.pointarret.PointArretResponse;
import com.coisee.covoiturage.enums.StatutTrajet;

import java.util.List;

public record TrajetResponse(
		Long id,
		Long conducteurId,
		Long vehiculeId,
		Integer placesProposees,
		Integer placesRestantes,
		StatutTrajet statut,
		List<PointArretResponse> pointsArret
) {
}
