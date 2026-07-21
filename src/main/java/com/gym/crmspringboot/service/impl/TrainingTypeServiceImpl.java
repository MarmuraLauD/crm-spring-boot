package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.TrainingType;
import com.gym.crmspringboot.repository.TrainingTypeRepository;
import com.gym.crmspringboot.service.TrainingTypeService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeRepository.findAll();
    }

}
