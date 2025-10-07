Feature: trips-service requests processing

  Background:
    Given the system is up

  Scenario: System received request from trip-service
    Given test driver server will return driver with id "1" and name "Artsiom"
    When client requests driver
    Then response contains id "1" and name "Artsiom"
    And driver with id "1" become busy
