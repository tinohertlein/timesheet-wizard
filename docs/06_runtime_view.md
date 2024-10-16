# Runtime View

## Level 1

`import` is invoked by a scheduler at a given point of time - e.g. sometimes at night, when a working day is completed.

Both modules are decoupled and do not have dependencies on each other.

This means, that `import` does not call `export` directly. Instead, (synchronous) events emitted by Spring Boot invoke
`export`, after a new timesheet is successfully imported by `import`.

![Dynamic-level-1](assets/dynamic-level-1.drawio.png "Dynamic-level-1")
