package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.client.TrainerWorkloadClient;
import com.gym.crmspringboot.dto.ActionType;
import com.gym.crmspringboot.dto.request.WorkloadRequest;
import com.gym.crmspringboot.model.Training;
import com.gym.crmspringboot.repository.TrainingRepository;
import com.gym.crmspringboot.repository.specification.TrainingSpecifications;
import com.gym.crmspringboot.security.util.SecurityUtils;
import com.gym.crmspringboot.service.TrainingService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerWorkloadClient workloadClient;

    @Override
    @Transactional
    public Training create(Training training) {
        log.info("Creating training: {}", training);
        Training savedTraining = trainingRepository.save(training);

        WorkloadRequest request = WorkloadRequest.builder()
                .trainerUsername(savedTraining.getTrainer().getUsername())
                .trainerFirstName(savedTraining.getTrainer().getFirstName())
                .trainerLastName(savedTraining.getTrainer().getLastName())
                .isActive(savedTraining.getTrainer().isActive())
                .trainingDate(savedTraining.getTrainingDate())
                .trainingDuration(savedTraining.getTrainingDuration().intValue())
                .actionType(ActionType.ADD)
                .build();

        workloadClient.updateWorkload(request, SecurityUtils.extractAuthToken());

        return savedTraining;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Training not found"));

        trainingRepository.delete(training);

        WorkloadRequest request = WorkloadRequest.builder()
                .trainerUsername(training.getTrainer().getUsername())
                .trainerFirstName(training.getTrainer().getFirstName())
                .trainerLastName(training.getTrainer().getLastName())
                .isActive(training.getTrainer().isActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration().intValue())
                .actionType(ActionType.DELETE)
                .build();

        workloadClient.updateWorkload(request, SecurityUtils.extractAuthToken());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> findById(Long id) {
        log.info("Finding training with ID: {}", id);
        return trainingRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainingsList(
            String username,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingType
    ) {
        Specification<Training> spec = TrainingSpecifications.filterTrainings(
                username, false, from, to, trainerName, trainingType
        );
        return trainingRepository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainingsList(
            String username,
            LocalDate from,
            LocalDate to,
            String traineeName,
            String trainingType
    ) {
        Specification<Training> spec = TrainingSpecifications.filterTrainings(
                username, true, from, to, traineeName, trainingType
        );
        return trainingRepository.findAll(spec);
    }

}