Feature: Rating: create rating

  Background:
    Given the system is up

  Scenario: Person creates a trip-rating
    When person creates request to create rating with comment "comment" and score "3"
    Then a rating record is saved to db with comment "comment" and score "3"