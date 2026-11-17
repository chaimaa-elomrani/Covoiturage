package com.coisee.covoiturage.dto.pointarret;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointArretRequest(
		@NotBlank(message = "La ville du point d’arrêt est obligatoire.")
		@Size(max = 120, message = "Le nom de la ville ne peut pas dépasser 120 caractères.")
		String ville,

		@NotNull(message = "L’ordre du point d’arrêt est obligatoire.")
		@Min(value = 0, message = "L’ordre du point d’arrêt doit être supérieur ou égal à zéro.")
		Integer ordre,

		@NotNull(message = "L’heure de passage est obligatoire.")
		LocalDateTime heurePassage,

		@NotNull(message = "Le prix depuis le départ est obligatoire.")
		@DecimalMin(value = "0.00", message = "Le prix depuis le départ ne peut pas être négatif.")
		@Digits(integer = 8, fraction = 2, message = "Le prix doit avoir au maximum 8 chiffres entiers et 2 décimales.")
		BigDecimal prixDepuisDepart
) {
}
