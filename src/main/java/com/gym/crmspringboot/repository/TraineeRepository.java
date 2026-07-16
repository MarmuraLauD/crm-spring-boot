package com.gym.crmspringboot.repository;

import com.gym.crmspringboot.model.Trainee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee,Integer> {

    Optional<Trainee> findByUsername(String username);
    Trainee update(Trainee trainee);
    void delete(String username);

}
