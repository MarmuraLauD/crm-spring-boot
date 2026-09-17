Feature: Trainer Registration Component Tests

  Scenario: Successful registration of a new trainer
    Given a valid trainer registration request with first name "John", last name "Doe", and specialization ID 1
    When a POST request is made for trainer registration to "/api/v1/trainers"
    Then the trainer registration response status should be 201
    And the trainer registration response should contain username and password

  Scenario: Registration fails due to missing first name
    Given an invalid trainer registration request with missing first name, last name "Doe", and specialization ID 1
    When a POST request is made for trainer registration to "/api/v1/trainers"
    Then the trainer registration response status should be 400