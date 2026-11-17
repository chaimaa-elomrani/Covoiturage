package com.coisee.covoiturage.mapper;


import com.coisee.covoiturage.dto.passager.PassagerRequest;
import com.coisee.covoiturage.dto.passager.PassagerResponse;
import com.coisee.covoiturage.entity.Passager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PassagerMapper {

    @Mapping(target = "id", ignore = true)
    Passager toEntity(PassagerRequest request);

    @Mapping( target = "id" , ignore = true)
    void updateEntity(PassagerRequest request , @MappingTarget Passager passager);

    PassagerResponse toResponse(Passager passager);
}
