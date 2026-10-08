package com.gym.crmspringboot.mapper;

import com.gym.crmspringboot.dto.request.TrainerRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTrainerRequest;
import com.gym.crmspringboot.dto.response.RegistrationResponse;
import com.gym.crmspringboot.dto.response.TrainerItemResponse;
import com.gym.crmspringboot.dto.response.TrainerProfileResponse;
import com.gym.crmspringboot.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainerMapper {

    @Mapping(target = "specialization.id", source = "specializationId")
    Trainer toEntity(TrainerRegistrationRequest request);

    @Mapping(target = "specialization", source = "specialization.trainingTypeName")
    TrainerProfileResponse toProfileResponse(Trainer trainer);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    void updateTrainerFromRequest(UpdateTrainerRequest request, @MappingTarget Trainer trainer);

    @Mapping(target = "specialization", source = "specialization.trainingTypeName")
    TrainerItemResponse toItemResponse(Trainer trainer);

    @Mapping(target = "password", source = "rawPassword")
    RegistrationResponse toRegistrationResponse(Trainer trainer);

}
