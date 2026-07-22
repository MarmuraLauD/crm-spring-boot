package com.gym.crmspringboot.repository;

import com.gym.crmspringboot.model.Trainer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer,Long> {

    Optional<Trainer> findByUsername(String username);
    @Query("SELECT t FROM Trainer t "
            + "LEFT JOIN t.trainees te WITH te.username = :username "
            + "WHERE t.active = true AND te.id IS NULL")
    List<Trainer> getUnassignedActiveTrainers(String username);
    List<Trainer> findByUsernameIn(@Param("usernames") List<String> usernames);

}
