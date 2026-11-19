package com.coisee.covoiturage.dto.trajet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TrajetSearchRequest(
		@NotBlank(message = "La ville de départ est obligatoire.")
		@Size(max = 120, message = "La ville de départ ne peut pas dépasser 120 caractères.")
		String villeDepart,

		@NotBlank(message = "La ville d’arrivée est obligatoire.")
		@Size(max = 120, message = "La ville d’arrivée ne peut pas dépasser 120 caractères.")
		String villeArrivee,

		@NotNull(message = "La date de départ est obligatoire.")
		LocalDate dateDepart,

		@NotNull(message = "Le nombre de places souhaitées est obligatoire.")
		@Min(value = 1, message = "Il faut demander au moins une place.")
		@Max(value = 8, message = "Le nombre de places demandé ne peut pas dépasser 8.")
		Integer nombrePlaces,

		@DecimalMin(value = "0.00", message = "Le prix maximal ne peut pas être négatif.")
		@Digits(integer = 8, fraction = 2, message = "Le prix maximal doit avoir au maximum 8 chiffres entiers et 2 décimales.")
		BigDecimal prixMax
) {
}
