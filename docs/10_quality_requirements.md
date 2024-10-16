# Quality Requirements

## Quality scenarios

| Nr  | Quality goal    | Motivation                                                                                                                                                                     | Scenario                                                                                                                                                                                                                                                          |
|-----|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| QG1 | Security        | Neither the Timesheet-Wizard nor data generated by it must be accessible outside of AWS. Code related to Tino's customers may neither be read- nor write-accessible to anyone. | No access to S3 buckets is permitted without AWS credentials and authorization by Tino. This is enforced by AWS S3 bucket policies. Code that is related to confidential customers is stored in a non-public repository.                                          |
| QG2 | Cost efficiency | The infrastructure costs to keep the Timesheet-Wizard running should be as low as possible.                                                                                    | The maximum monthly budget for the Timesheet-Wizard is 10€. A monitoring is established by using the AWS cost alerting mechanism.                                                                                                                                 |
| QG3 | Extensibility   | The effort to add new target formats for the timesheets should be as low as possible.                                                                                          | This is of course depending on the complexity of the new target format. Leaving the effort of the actual implementation out of the game, adding this new feature to the current application is to be done in less than 1 working day including deployment to AWS. |
| QG4 | Testability     | The components of the Timesheet-Wizard as well as the Timesheet-Wizard itself should be as easy as possible with a very high degree of automation.                             | The only manual testing step is the testing of the final XLSX, PDF or CSV files for plausibility and correctness once every month before doing the book keeping. All other tests are executed automatically in the CI/CD-pipeline.                                |
