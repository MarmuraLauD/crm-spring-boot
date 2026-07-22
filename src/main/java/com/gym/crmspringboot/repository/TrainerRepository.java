package com.gym.crmspringboot.repository;

import com.gym.crmspringboot.model.Trainer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer,Long> {

    Optional<Trainer> findByUsername(String username);
    @Query("SELECT t FROM Trainer t "
            + "WHERE t.isActive = true AND t NOT IN "
            + "(SELECT tr FROM Trainee te JOIN te.trainers tr WHERE te.username = :username)"
    )
    List<Trainer> getUnassignedTrainers(String username);
    List<Trainer> findByUsernames(List<String> usernames);

}
