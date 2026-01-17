# Solution Strategy

The business logic should be decoupled from the cloud infrastructure and also from framework things, so that a
deployment to various hyperscalers or using a different web framework
doesn't have any impact on the core logic.

To achieve this, the business logic is implemented in a separate Gradle
subproject `tw-core`. It is used by an application project that bundles the `tw-core` project and any additional cloud
and framework dependencies.

This app (e.g. `tw-app-aws`) is then deployed to AWS as a Lambda function.
Any code that is cloud- or app-related is decoupled by the service-provider-interface Gradle subproject `tw-spi`. (**->
Quality goal QG3**).

See chapter 5 for a building block view.

The individual parts of importing timesheets and transforming them to other formats should be decoupled so that they
can be updated or even replaced independently of each other. To achieve this, both modules are realized in the Gradle
subproject `tw-core` as separate
Kotlin packages on the top level that are not allowed to have
dependencies on each other. Code that is mapping domain classes from module `importing` to module `exporting` is placed
in a
package `anticorruption`. This is enforced automatically
by [Architecture tests](../tw-core/src/test/kotlin/dev/hertlein/timesheetwizard/core/ArchitectureTest.kt). (**-> Quality
goal QG3**).

This results in three top-level packages:

- importing
- exporting
- anticorruption

To facilitate communication between `importing` and `exporting` events are used to signal successful
imports of timesheets.

The Timesheet-Wizard is not accessible outside AWS or Azure (**-> Quality goal #QG1**). Neither the S3 buckets
containing
timesheets are visible from the outside, nor the initiation of the workflow can be triggered without proper AWS
credentials and user access rights.

Though most code of the Timesheet-Wizard is publicly exposed via this public GitHub repository, any confidential
customer-related stuff is configured via private configuration files.

As the Timesheet-Wizard should be easily extensible, testable and maintainable, both business modules will follow
a [Ports & Adapters](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) architecture, to separate domain
logic and connecting to surrounding systems. This is enforced automatically
by [Architecture tests](../tw-core/src/test/kotlin/dev/hertlein/timesheetwizard/core/ArchitectureTest.kt). (**-> Quality goals QG3**).

For monitoring, logging, error notification and other shared concepts, AWS or Azure systems
like [AWS CloudWatch](https://aws.amazon.com/cloudwatch/?nc1=h_ls)
, [AWS Simple Notification Service](https://aws.amazon.com/sns/?nc1=h_ls), ... are used. They are set up following an
infrastructure-as-code approach
using [AWS-SAM](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html)
and [AWS CloudFormation](https://aws.amazon.com/cloudformation/?nc1=h_ls) in case of AWS
or [Azure Bicep](https://learn.microsoft.com/en-us/azure/azure-resource-manager/bicep/) in case of Azure or [Terraform](https://developer.hashicorp.com/terraform) in case of Google Cloud Platform
All of these are free of charge up to a certain limit – which won't be reached by a tiny application like the
Timesheet-Wizard (**-> Quality goal #QG2**). 
