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

  Scenario: Fail to update trainee profile due to missing first name
    Given a registered trainee user with username "Bob.Jones"
    And an invalid update trainee request for "Bob.Jones" with missing first name
    When a PUT request is made for trainee update to "/api/v1/trainees/Bob.Jones"
    Then the trainee response status should be 400

  Scenario: Successfully get trainee profile
    Given a registered trainee user with username "Alice.Smith"
    When a GET request is made by trainee for profile to "/api/v1/trainees/Alice.Smith"
    Then the trainee response status should be 200
    And the trainee response should contain the profile information

  Scenario: Fail to get trainee profile due to non-existent username
    When a GET request is made by trainee for profile to "/api/v1/trainees/Non.Existent"
    Then the trainee response status should be 404

  Scenario: Successfully update trainee trainers list
    Given a registered trainee user with username "Alice.Smith"
    And a list of trainer usernames "John.Doe" and "Jane.Doe"
    When a PUT request is made by trainee to update trainers list to "/api/v1/trainees/Alice.Smith/trainers"
    Then the trainee response status should be 200
    And the trainee response should be a list of assigned trainers

  Scenario: Successfully delete trainee profile
    Given a registered trainee user with username "Alice.Smith"
    When a DELETE request is made by trainee to "/api/v1/trainees/Alice.Smith"
    Then the trainee response status should be 200