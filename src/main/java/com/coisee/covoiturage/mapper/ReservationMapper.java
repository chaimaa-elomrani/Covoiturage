package com.coisee.covoiturage.mapper;

import com.coisee.covoiturage.dto.reservation.ReservationRequest;
import com.coisee.covoiturage.dto.reservation.ReservationResponse;
import com.coisee.covoiturage.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ReservationMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "passager", ignore = true)
	@Mapping(target = "trajet", ignore = true)
	@Mapping(target = "pointMontee", ignore = true)
	@Mapping(target = "pointDescente", ignore = true)
	@Mapping(target = "montantTotal", ignore = true)
	@Mapping(target = "dateReservation", ignore = true)
	@Mapping(target = "statut", ignore = true)
	Reservation toEntity(ReservationRequest request);

	@Mapping(target = "passagerId", source = "reservation.passager.id")
	@Mapping(target = "trajetId", source = "reservation.trajet.id")
	@Mapping(target = "pointMonteeId", source = "reservation.pointMontee.id")
	@Mapping(target = "pointDescenteId", source = "reservation.pointDescente.id")
	ReservationResponse toResponse(Reservation reservation);
}
