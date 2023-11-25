# Runtime View

## Level 1

'importer' is invoked by a scheduler at a given point of
time - e.g. sometimes at night, when a working day is completed.

Both modules are decoupled and do not have dependencies on each other.

This means, that 'importer' does not call 'generate-exports' directly. Instead, events emitted by AWS S3 invoke '
generate-exports', after a new timesheet is imported by 'importer'. To be more precise: an S3 'Put event' is
sent.

![Dynamic-level-1](assets/dynamic-level-1.drawio.png "Dynamic-level-1")
