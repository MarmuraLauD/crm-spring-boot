package com.gym.crmspringboot.repository;

import com.gym.crmspringboot.model.Trainer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer,Long> {

    Trainer update(Trainer trainer);
    Optional<Trainer> findByUsername(String username);
    List<Trainer> getUnassignedTrainers(String username);
    List<Trainer> findByUsernames(List<String> usernames);

}
