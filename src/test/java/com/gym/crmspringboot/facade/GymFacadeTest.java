package com.gym.crmspringboot.facade;

import com.gym.crmspringboot.model.Trainee;
import com.gym.crmspringboot.model.Trainer;
import com.gym.crmspringboot.service.TraineeService;
import com.gym.crmspringboot.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private UserService userService;

    @InjectMocks
    private GymFacade gymFacade;

    @Test
    void registerTrainee_DelegatesToService() {
        // Arrange
        Trainee trainee = new Trainee();
        when(traineeService.createTrainee(trainee)).thenReturn(trainee);

        // Act
        Trainee result = gymFacade.registerTrainee(trainee);

        // Assert
        assertEquals(trainee, result);
        verify(traineeService).createTrainee(trainee);
    }

    @Test
    void changeUserPassword_DelegatesToService() {
        // Arrange
        String username = "user";
        String oldPass = "old";
        String newPass = "new";

        // Act
        gymFacade.changeUserPassword(username, oldPass, newPass);

        // Assert
        verify(userService).changePassword(username, oldPass, newPass);
    }

    @Test
    void updateTraineesTrainerList_DelegatesToService() {
        // Arrange
        String username = "user";
        List<String> trainers = List.of("trainer1");
        List<Trainer> expected = List.of(new Trainer());

        when(traineeService.updateTrainersList(username, trainers)).thenReturn(expected);

        // Act
        List<Trainer> result = gymFacade.updateTraineesTrainerList(username, trainers);

        // Assert
        assertEquals(expected, result);
        verify(traineeService).updateTrainersList(username, trainers);
    }
}