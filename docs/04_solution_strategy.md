# Solution Strategy

The individual parts of importing timesheets and transforming them to other formats should be decoupled, so that they
can be updated or even replaced independently of each other. To achieve this, both modules are realized as separate
Kotlin packages on the top level that are not allowed to have
dependencies on each other. Shared code is placed in a package 'shared'. (**-> Quality goals QG3 & QG5**).

This results in three top level packages:

- import
- export
- shared

where 'import' and 'export' are allowed to access 'shared' but not each other.
To facilitate communication between 'import' and 'export' Spring Boot application events are used to signal successful
imports of timesheets.

The Timesheet-Wizard is not accessible outside of AWS (**-> Quality goal #QG1**). Neither the S3 buckets containing
timesheets are visible from the outside nor the initiation of the workflow can be triggered without proper AWS
credentials and user access rights.

Though most code of the Timesheet-Wizard is publicly exposed via this public GitHub-repository, any confidential
customer related stuff is configured via private configuration files.

As the Timesheet-Wizard should be easily extensible, testable and maintainable, both business modules will follow
a [Ports & Adapters](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) architecture, to separate domain
logic and connecting to surrounding systems (**-> Quality goals QG3 & QG5**).

For monitoring, logging, error notification and other shared concepts, AWS systems
like [AWS CloudWatch](https://aws.amazon.com/cloudwatch/?nc1=h_ls)
, [AWS Simple Notification Service](https://aws.amazon.com/sns/?nc1=h_ls), ... are used. They are set up following an
infrastructure-as-code approach using [AWS-SAM](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html) and [AWS CloudFormation](https://aws.amazon.com/cloudformation/?nc1=h_ls). 
All of these are free of charge up to a certain limit - which won't be reached by a tiny application like the Timesheet-Wizard (**-> Quality goal #QG2**). 
