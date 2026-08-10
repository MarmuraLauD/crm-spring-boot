package com.gym.crmspringboot.mapper;

import com.gym.crmspringboot.dto.request.TraineeRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTraineeRequest;
import com.gym.crmspringboot.dto.response.RegistrationResponse;
import com.gym.crmspringboot.dto.response.TraineeProfileResponse;
import com.gym.crmspringboot.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = TrainerMapper.class)
public interface TraineeMapper {

    Trainee toEntity(TraineeRegistrationRequest request);

    @Mapping(target = "username", ignore = true)
    Trainee toEntity(UpdateTraineeRequest request);

    TraineeProfileResponse toProfileResponse(Trainee trainee);

    @Mapping(target = "password", source = "rawPassword")
    RegistrationResponse toRegistrationResponse(Trainee trainee);

}
