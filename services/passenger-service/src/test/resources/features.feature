Feature: Passenger creates trip

  Background:
    Given the system is up

  Scenario: Passenger creates a trip and event is published
    Given a passenger exists with name "ivan" and phone "+375447006485"
    When the passenger requests a trip from "From A" to "To B"
    Then a message should be published to topic "trips.make" containing "ivan"
    And a message is saved to db containing record with id "1"
