# Cross-cutting Concepts

## Security

The Timesheet-Wizard is not available outside AWS. The whole workflow is initiated and processed within AWS. The only
exception is the manual download of generated XLSX and PDF files. This has to be done with a manual login into the AWS console.

## Logging

For logging, alarming and monitoring AWS systems are used. The retention period for log files is 30 days. If an error is
recognized, an email is sent to Tino's email-address. No further error-handling is needed.

## Persistence

AWS S3 is used to store data objects - being it 'final' XLSX, PDF or CSV files or 'intermediate' data objects like
json-representations of the imported timesheets.
