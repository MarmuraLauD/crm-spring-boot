package com.gym.crmspringboot.mapper;

import com.gym.crmspringboot.dto.request.AddTrainingRequest;
import com.gym.crmspringboot.dto.response.TraineeTrainingItemResponse;
import com.gym.crmspringboot.dto.response.TrainerTrainingItemResponse;
import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.trainingName")
    @Mapping(target = "trainingDate", source = "request.trainingDate")
    @Mapping(target = "trainingDuration", source = "request.trainingDuration")
    @Mapping(target = "trainee", source = "trainee")
    @Mapping(target = "trainer", source = "trainer")
    @Mapping(target = "trainingType", source = "trainer.specialization")
    Training toEntity(AddTrainingRequest request, Trainee trainee, Trainer trainer);

    @Mapping(target = "trainingType", source = "trainingType.trainingTypeName")
    @Mapping(target = "trainerName", source = "trainer.username")
    TraineeTrainingItemResponse toTraineeTrainingItemResponse(Training training);

    @Mapping(target = "trainingType", source = "trainingType.trainingTypeName")
    @Mapping(target = "traineeName", source = "trainee.username")
    TrainerTrainingItemResponse toTrainerTrainingItemResponse(Training training);

}
