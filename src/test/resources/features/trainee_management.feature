Feature: Trainee Management Component Tests

  Scenario: Successfully register a new trainee
    Given a valid trainee registration request with first name "Alice", last name "Smith", date of birth "1995-05-12", and address "Main St 1"
    When a POST request is made for trainee registration to "/api/v1/trainees"
    Then the trainee response status should be 201
    And the trainee response should contain username and password

  Scenario: Successfully update trainee profile
    Given a registered trainee user with username "Alice.Smith"
    And a valid update trainee request for "Alice.Smith"
    When a PUT request is made for trainee update to "/api/v1/trainees/Alice.Smith"
    Then the trainee response status should be 200
    And the trainee response should reflect the updated profile