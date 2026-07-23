package com.gym.crmspringboot.mapper;

import com.gym.crmspringboot.dto.response.TrainingTypeItemResponse;
import com.gym.crmspringboot.model.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingTypeMapper {

    @Mapping(target = "trainingTypeId", source = "id")
    @Mapping(target = "trainingType", source = "trainingTypeName")
    TrainingTypeItemResponse toItemResponse(TrainingType trainingType);

}
