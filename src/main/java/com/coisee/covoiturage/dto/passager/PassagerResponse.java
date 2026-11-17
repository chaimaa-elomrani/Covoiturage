package com.coisee.covoiturage.dto.passager;

public record PassagerResponse(
		Long id,
		String prenom,
		String nom,
		String email,
		String telephone
) {
}
