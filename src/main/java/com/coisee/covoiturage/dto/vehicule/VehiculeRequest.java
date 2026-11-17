package com.coisee.covoiturage.dto.vehicule;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record VehiculeRequest(
		@NotBlank(message = "La marque est obligatoire.")
		@Size(max = 80, message = "La marque ne peut pas dépasser 80 caractères.")
		String marque,

		@NotBlank(message = "Le modèle est obligatoire.")
		@Size(max = 100, message = "Le modèle ne peut pas dépasser 100 caractères.")
		String modele,

		@NotBlank(message = "L’immatriculation est obligatoire.")
		@Size(max = 20, message = "L’immatriculation ne peut pas dépasser 20 caractères.")
		String immatriculation,

		@NotBlank(message = "La couleur est obligatoire.")
		@Size(max = 50, message = "La couleur ne peut pas dépasser 50 caractères.")
		String couleur,

		@NotNull(message = "La capacité maximale est obligatoire.")
		@Min(value = 1, message = "La capacité minimale est de 1 place passager.")
		@Max(value = 8, message = "La capacité maximale est de 8 places passagers.")
		Integer capaciteMaximale,

		@NotNull(message = "L’identifiant du conducteur propriétaire est obligatoire.")
		@Positive(message = "L’identifiant du conducteur doit être positif.")
		Long conducteurId
) {
}
