package com.gym.crmspringboot.mapper;

import com.gym.crmspringboot.dto.request.TrainerRegistrationRequest;
import com.gym.crmspringboot.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TrainerMapper {

    @Mapping(target = "specialization.id", source = "specializationId")
    Trainer toEntity(TrainerRegistrationRequest request);

}
