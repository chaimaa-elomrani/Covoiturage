package com.coisee.covoiturage.mapper;

import com.coisee.covoiturage.dto.trajet.TrajetRequest;
import com.coisee.covoiturage.dto.trajet.TrajetResponse;
import com.coisee.covoiturage.entity.Trajet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		uses = PointArretMapper.class,
		unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TrajetMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "conducteur", ignore = true)
	@Mapping(target = "vehicule", ignore = true)
	@Mapping(target = "statut", ignore = true)
	Trajet toEntity(TrajetRequest request);

	@Mapping(target = "conducteurId", source = "trajet.conducteur.id")
	@Mapping(target = "vehiculeId", source = "trajet.vehicule.id")
	@Mapping(target = "placesRestantes", source = "placesRestantes")
	TrajetResponse toResponse(Trajet trajet, Integer placesRestantes);
}
