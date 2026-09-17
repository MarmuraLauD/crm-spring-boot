Feature: Training Management Component Tests

  Scenario: Successfully get training types
    When a GET request is made for training types to "/api/v1/trainings/types"
    Then the training response status should be 200
    And the response should contain a list of training types

  Scenario: Successfully add a new training
    Given a valid add training request for trainee "Alice.Smith" and trainer "John.Doe"
    When a POST request is made for adding training to "/api/v1/trainings"
    Then the training response status should be 200

  Scenario: Successfully get trainee trainings with filters
    Given a registered trainee user with username "Alice.Smith"
    When a GET request is made by trainer for trainee trainings to "/api/v1/trainings/trainee/Alice.Smith"
    Then the training response status should be 200
    And the response should be a list of trainee trainings

  Scenario: Successfully get trainer trainings with filters
    Given a registered trainer user with username "John.Doe"
    When a GET request is made by trainer for trainer trainings to "/api/v1/trainings/trainer/John.Doe"
    Then the training response status should be 200
    And the response should be a list of trainer trainings