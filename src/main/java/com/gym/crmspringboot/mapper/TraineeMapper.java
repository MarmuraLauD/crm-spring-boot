package com.gym.crmspringboot.mapper;

import com.gym.crmspringboot.dto.request.TraineeRegistrationRequest;
import com.gym.crmspringboot.model.Trainee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TraineeMapper {

    Trainee toEntity(TraineeRegistrationRequest request);

}
