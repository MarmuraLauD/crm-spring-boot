package com.gym.crmspringboot.mapper;

import com.gym.crmspringboot.dto.request.TraineeRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTraineeRequest;
import com.gym.crmspringboot.dto.response.RegistrationResponse;
import com.gym.crmspringboot.dto.response.TraineeProfileResponse;
import com.gym.crmspringboot.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        uses = TrainerMapper.class)
public interface TraineeMapper {

    Trainee toEntity(TraineeRegistrationRequest request);

    @Mapping(target = "username", ignore = true)
    Trainee toEntity(UpdateTraineeRequest request);

    TraineeProfileResponse toProfileResponse(Trainee trainee);

    RegistrationResponse toRegistrationResponse(Trainee trainee);

}
