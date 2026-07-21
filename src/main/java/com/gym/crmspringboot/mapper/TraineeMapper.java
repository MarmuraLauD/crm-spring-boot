package com.gym.crmspringboot.mapper;

import com.gym.crmspringboot.dto.request.TraineeRegistrationRequest;
import com.gym.crmspringboot.dto.request.UpdateTraineeRequest;
import com.gym.crmspringboot.dto.response.TraineeItemResponse;
import com.gym.crmspringboot.dto.response.TraineeProfileResponse;
import com.gym.crmspringboot.model.Trainee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        uses = TrainerMapper.class)
public interface TraineeMapper {

    Trainee toEntity(TraineeRegistrationRequest request);

    Trainee toEntity(UpdateTraineeRequest request);

    TraineeProfileResponse toProfileResponse(Trainee trainee);

    TraineeItemResponse toItemResponse(Trainee trainee);

}
