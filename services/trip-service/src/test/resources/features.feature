Feature: trip-service trying to create trip

  Background:
    Given the system is up

  Scenario: trip-service trying to create trip from kafka "trips.make"
    Given write trip order to trips.make with passenger name "Tomas" and passenger phone "+375447006485"
    When service make request for get free driver to grpc-server
    Then trip save to db with driver info: name "Tomas" and passenger phone "+375447006485"
    And trip record with id is saved to kafka topic "trips.created"
