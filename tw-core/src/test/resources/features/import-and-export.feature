Feature: Import and export timesheet entries

  Scenario: import timesheet entries from Clockify & export them to a memory persistence store
    Given timesheet entries are logged in Clockify for customer with id 1000
    When the application is started with customer id 1000 and date range 'CUSTOM_YEAR' with a value of '2022'
    Then the timesheet entries are imported from Clockify, transformed and then exported to a memory persistence store