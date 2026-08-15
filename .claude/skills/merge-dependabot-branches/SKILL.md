---
name: merge-dependabot-branches
description: Merge branches created by Dependabot into the main branch
---

## Key Principles

- Always check the current state (`git status`, `git log --oneline -10`) before performing an operation.
- Dependabot branches are identified by their naming convention: `dependabot/**`.
- Use `git rebase` to rebase the master branch on each Dependabot branch.
- After every single successful rebase, execute `gradle check` to verify that all tests still succeed.
- If the tests fail, abort the rebase of this Dependabot branch and continue with the next Dependabot branch.
- Never use `git push --force`

## Conflict Resolution

- Use `git diff` and `git log --merge` to understand the conflicting changes.
- Resolve conflicts in an editor or merge tool, then `git add` the resolved files and `git rebase --continue`.
- If a rebase goes wrong, `git rebase --abort` returns to the pre-rebase state.