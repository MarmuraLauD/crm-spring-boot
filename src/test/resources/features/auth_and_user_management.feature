Feature: Authentication and User Management Component Tests

  Scenario: Successfully login a user
    Given a valid auth request for username "John.Doe" and password "password123"
    When a POST request is made to login at "/api/v1/auth/login"
    Then the auth response status should be 200
    And the response should contain an access token and a refresh cookie

  Scenario: Fail to login with invalid credentials
    Given an invalid auth request for username "John.Doe" and password "wrong_password"
    When a POST request is made to login at "/api/v1/auth/login"
    Then the auth response status should be 401

  Scenario: Successfully refresh token
    Given a valid refresh token cookie
    When a POST request is made to refresh at "/api/v1/auth/refresh"
    Then the auth response status should be 200
    And the response should contain a new access token

  Scenario: Successfully logout
    Given a valid refresh token cookie
    When a POST request is made to logout at "/api/v1/auth/logout"
    Then the auth response status should be 200

  Scenario: Successfully change password
    When a PUT request is made to change password at "/api/v1/users/password" with username "John.Doe", old password "password123", and new password "newPassword123"
    Then the user response status should be 200

  Scenario: Successfully toggle user status by trainer
    When a PATCH request is made by trainer to toggle status at "/api/v1/users/John.Doe/status" to false
    Then the user response status should be 200