package com.coisee.covoiturage.mapper;

import com.coisee.covoiturage.dto.conducteur.ConducteurRequest;
import com.coisee.covoiturage.dto.conducteur.ConducteurResponse;
import com.coisee.covoiturage.entity.Conducteur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ConducteurMapper {

    @Mapping(target = "id", ignore = true)
    Conducteur toEntity(ConducteurRequest request);

    ConducteurResponse toResponse(Conducteur conducteur);

}
