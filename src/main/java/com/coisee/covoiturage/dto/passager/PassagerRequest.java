package com.coisee.covoiturage.dto.passager;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PassagerRequest(
		@NotBlank(message = "Le prénom est obligatoire.")
		@Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères.")
		String prenom,

		@NotBlank(message = "Le nom est obligatoire.")
		@Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères.")
		String nom,

		@NotBlank(message = "L’adresse email est obligatoire.")
		@Email(message = "L’adresse email doit être valide.")
		@Size(max = 254, message = "L’adresse email ne peut pas dépasser 254 caractères.")
		String email,

		@NotBlank(message = "Le numéro de téléphone est obligatoire.")
		@Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères.")
		String telephone
) {
}
