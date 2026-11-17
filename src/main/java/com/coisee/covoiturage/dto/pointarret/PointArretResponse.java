package com.coisee.covoiturage.dto.pointarret;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointArretResponse(
		Long id,
		String ville,
		Integer ordre,
		LocalDateTime heurePassage,
		BigDecimal prixDepuisDepart
) {
}
