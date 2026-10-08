Feature: Trainer Profile and Management Component Tests

  Scenario: Successfully get trainer profile
    Given a registered trainer user with username "John.Doe"
    When a GET request is made by trainer for profile to "/api/v1/trainers/John.Doe"
    Then the management response status should be 200
    And the response should contain the trainer profile information

  Scenario: Fail to get trainer profile due to non-existent username
    When a GET request is made by trainer for profile to "/api/v1/trainers/Non.Existent"
    Then the management response status should be 404

  Scenario: Update trainer profile successfully
    Given a registered trainer user with username "Jane.Doe"
    And a valid update trainer request for "Jane.Doe"
    When a PUT request is made by trainer for update to "/api/v1/trainers/Jane.Doe"
    Then the management response status should be 200
    And the response should reflect the updated trainer profile

  Scenario: Update trainer profile fails due to missing first name
    Given a registered trainer user with username "Jack.Doe"
    And an invalid update trainer request for "Jack.Doe" with missing first name
    When a PUT request is made by trainer for update to "/api/v1/trainers/Jack.Doe"
    Then the management response status should be 400

  Scenario: Get unassigned trainers for a trainee
    Given a trainee user exists with username "Trainee.User"
    When a GET request is made for unassigned trainers with traineeUsername "Trainee.User"
    Then the management response status should be 200
    And the response should be a list of trainers