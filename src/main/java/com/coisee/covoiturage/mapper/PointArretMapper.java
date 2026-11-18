package com.coisee.covoiturage.mapper;

import com.coisee.covoiturage.dto.pointarret.PointArretRequest;
import com.coisee.covoiturage.dto.pointarret.PointArretResponse;
import com.coisee.covoiturage.entity.PointArret;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PointArretMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "trajet", ignore = true)
	PointArret toEntity(PointArretRequest request);

	PointArretResponse toResponse(PointArret pointArret);
}
