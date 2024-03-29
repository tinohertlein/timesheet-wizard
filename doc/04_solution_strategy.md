# Solution Strategy

The individual parts of importing timesheets and transforming them to other formats should be highly decoupled. To
achieve this, events emitted by AWS systems, AWS S3 as shared storage and different Lambda functions for the tasks, are
used for communication. Currently, there are two separate tasks, each of them being implemented as a Lambda function:

- importer
- documents-generator

To support my personal learning, I decided to implement each Lambda function with a different framework, I have not yet
been working with. `importer` will be realized as [Micronaut application](https://micronaut.io/), `
documents-generator` as [Quarkus application](https://quarkus.io/). As both frameworks support the generation
of [GraalVM](https://www.graalvm.org/) native images, this will be tried out as well (**-> Quality goal #QG4**).

Both Lambda functions neither will have dependencies on each other, nor will they share code in a common library or the
like. They communicate via events emitted by [AWS S3](https://aws.amazon.com/s3/?nc1=h_ls) and share S3 as storage to
exchange data objects.

The Timesheet-Wizard is not accessible outside of AWS (**-> Quality goal #QG1**). Neither the S3 buckets containing
timesheets are visible from the outside nor the initiation of the workflow can be triggered without proper AWS
credentials and user access rights.

Though most code of the Timesheet-Wizard is publicly exposed via this public GitHub-repository, any confidential customer related code resides in a private GitHub-repository. 
The dependencies are visualized in the following diagram:

![Solution](assets/solution.drawio.png "Solution")

* `documents-generator-spi` serves as a service-provider-interface that is used as a compile-time-dependency in the
  other
  modules.
* At runtime, the libraries `documents-generator-customers-public` (for non-confidential/test customers) & `
  documents-generator-customers-private` (for confidential customers) are used by the `documents-generator`app,
  so that all customer-specific-code for the documents generation is executed.


  As the Timesheet-Wizard should be easily extensible, testable and maintainable, each function will follow
a [Ports & Adapters](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) architecture, to separate domain
logic and connecting to surrounding systems (**-> Quality goals QG3 & QG5**).

For monitoring, logging, error notification and other shared concepts, AWS systems
like [AWS CloudWatch](https://aws.amazon.com/cloudwatch/?nc1=h_ls)
, [AWS Simple Notification Service](https://aws.amazon.com/sns/?nc1=h_ls), ... are used. They are set up following an
infrastructure-as-code approach
using [AWS-SAM](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html)
and [AWS CloudFormation](https://aws.amazon.com/cloudformation/?nc1=h_ls). All of these are free of charge up to a
certain limit - which won't be reached by a tiny application like the Timesheet-Wizard (**-> Quality goal #QG2**). 
