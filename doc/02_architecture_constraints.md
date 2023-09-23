# Architecture Constraints

## Technical Constraints

| Nr  | Constraint               | Explanation                                                                                                      | Motivation                         |                                                                          
|-----|--------------------------|------------------------------------------------------------------------------------------------------------------|------------------------------------|
| TC1 | Usage of AWS lambda      | The Timesheet-Wizard has to be running in the AWS cloud as Lambda function(s) with support of other AWS services | I want to sharpen my AWS-knowledge |
| TC2 | Implementation in Kotlin | The Timesheet-Wizard has to implemented in Kotlin                                                                | I want to sharpen my Kotlin skills |

## Organizational Constraints

| Nr  | Constraint                             | Explanation                                                                                                                          |                                                                          
|-----|----------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| OC1 | Team                                   | It's only me: Tino Hertlein                                                                                                          |
| OC2 | Time schedule                          | Start is during my time-off in summer 2022. The Timesheet-Wizard should be usable by autumn 2022 when starting to work for clients   |
| OC3 | Technical setup                        | Docker, AWS-SAM, Java 17 are mandatory for local testing                                                                             |
| OC4 | Configuration and version control      | Store source code in a public Github repo. Follow infrastructure-as-code via AWS Cloud Formation. Realize CI/CD using Github Actions |
| OC5 | Testing                                | Use Detekt, JUnit, AssertJ, RestAssured, Mockk, Testcontainers                                                                       |

## Conventions

| Nr  | Constraint                 | Explanation                                                 |                                                                          
|-----|----------------------------|-------------------------------------------------------------|
| C1  | Architecture documentation | Use arc42 with Markdown files                               |
| C2  | Coding conventions         | Use Kotlin Coding Conventions given by Detekt               |
| C3  | Language                   | English. Coding & Documenting in German feels totally weird |
