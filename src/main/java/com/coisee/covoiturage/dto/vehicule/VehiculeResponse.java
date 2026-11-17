package com.coisee.covoiturage.dto.vehicule;

public record VehiculeResponse(
		Long id,
		String marque,
		String modele,
		String immatriculation,
		String couleur,
		Integer capaciteMaximale,
		Long conducteurId
) {
}
