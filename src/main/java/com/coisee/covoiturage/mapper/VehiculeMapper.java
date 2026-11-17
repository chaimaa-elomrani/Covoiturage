package com.coisee.covoiturage.mapper;

import com.coisee.covoiturage.dto.conducteur.ConducteurRequest;
import com.coisee.covoiturage.dto.vehicule.VehiculeRequest;
import com.coisee.covoiturage.dto.vehicule.VehiculeResponse;
import com.coisee.covoiturage.entity.Conducteur;
import com.coisee.covoiturage.entity.Vehicule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface VehiculeMapper {

    @Mapping( target = "id" , ignore = true)
    Vehicule toEntity(VehiculeRequest request);

    @Mapping( target = "id" , ignore = true)
    void updateEntity(ConducteurRequest request , @MappingTarget Vehicule vehicule); 

    VehiculeResponse toResponse(VehiculeRequest vehicule);

}
