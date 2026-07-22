package com.gym.crmspringboot.repository;

import com.gym.crmspringboot.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrainingRepository extends JpaRepository<Training,Long>,
        JpaSpecificationExecutor<Training> {

}
