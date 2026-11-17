package com.coisee.covoiturage.dto.conducteur;

public record ConducteurResponse(
		Long id,
		String prenom,
		String nom,
		String email,
		String telephone,
		String numeroPermis
) {
}
