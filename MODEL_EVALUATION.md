# Seed4J CLI Model Evaluation

## Protocol freeze

This document defines the evaluation protocol before any implementation source,
native test, Seed4J module history, or conversation transcript is inspected for
scoring. Result observations and scores will be added in a later commit without
changing the rules in this section.

## Objective and scope

This experiment compares three Codex models implementing the same Hangman
specification while using the same frozen Seed4J CLI skill, prompt, runtime, and
reasoning effort. It evaluates the observed outcomes and the models' use of the
CLI. It has no no-Seed4J control and therefore cannot establish that Seed4J
caused any observed difference.

The optional user interface in `SPEC.md` is outside the weighted evaluation.
Null or empty secrets, nonpositive limits, lowercase guesses, guesses after a
terminal state, punctuation in secrets, and other unspecified cases may be
reported only as unweighted robustness observations.

## Immutable inputs

- Repository: `https://github.com/renanfranca/seed4j-cli-hangman-kata`
- Specification path: `SPEC.md`
- Specification SHA-256: `692a642823832ec6e1e685b3bd8f94139a2260e86be99d022ecca497254994c3`
- Main commit: `340804336f01a8cd957d6522c5ee6cdb7b15c7df`
- Common base branch: `hangman-kata-seed4j-base`
- Common base commit: `8a3c3b5cfc1f520d3ab4d069ddc219de724c6e9e`
- Seed4J CLI: `0.0.4`
- Seed4J runtime: `2.2.0` in `standard` mode
- Seed4J skill tree SHA-256: `93b17f5b4a53d747f2b33f815984c56b297f52859b1f1825f8cce44eadba078c`
- Prompt language: English
- Prompt SHA-256: `06ac1ad554b276bfd6227d0eda17408d4421bfd95a5a5a5afe1c417061003283`

The exact shared prompt is:

```text
Implement the specification in SPEC.md using the already-installed Seed4J CLI tool as support.
```

## Model matrix

| Index | Alias | Model | Effort | Result branch | Implementation commit | Audit head |
| ---: | --- | --- | --- | --- | --- | --- |
| 1 | `luna-xhigh` | `gpt-5.6-luna` | `xhigh` | `hangman-kata-luna-xhigh` | `9613f8eeaa5d1375c9e595ea5ab44ce63b0896dc` | `6f8722d55a1e9fd0f5be9842e689db3014cdaec7` |
| 2 | `terra-xhigh` | `gpt-5.6-terra` | `xhigh` | `hangman-kata-terra-xhigh` | `f463cb061ade07c1adf060664f8da718338664a4` | `ca749c49bf81b0230ef600817ca1b7b37c862c70` |
| 3 | `sol-xhigh` | `gpt-5.6-sol` | `xhigh` | `hangman-kata-sol-xhigh` | `ea7bda4e64df04ae9a3058e6878edb79efe0eb21` | `afe186dbc40d8bae47f31f501490c53b75854cfb` |

Runs are evaluated in execution-index order. Ties remain ties and are displayed
in that same order. The matrix supports a same-effort model comparison but no
same-model effort comparison.

## Public observation surface

The common acceptance protocol uses Java 25 and one assertion harness. Each
implementation receives a thin adapter, created only inside its disposable
extraction, that exposes these canonical observations:

- construct a game from a secret string and incorrect-guess limit;
- submit one character and classify the public result as `CORRECT`,
  `INCORRECT`, `INVALID`, or `DUPLICATE`;
- observe `IN_PROGRESS`, `WON`, or `LOST` through the public API;
- read the publicly exposed incorrect-guess history.

An adapter may call or rename public APIs and canonicalize their public values.
It may not use reflection, access private state, maintain its own game state,
compute Hangman behavior, or compensate for missing behavior. If a required
observation is not publicly available, its requirement fails. Source inspection
may explain a failure but cannot replace executable public evidence.

Production sources, the adapter, and the common harness are compiled with
`javac --release 25`. The harness is run with assertions enabled. Harness files
and compiler output remain outside every result branch.

## Functional requirements — 27 points

Each requirement is binary and worth exactly 2.7 points. The subtotal therefore
remains exactly 27 points. Full credit requires every frozen public observation
for that requirement to pass; otherwise the requirement receives zero.

| ID | Requirement | Points |
| --- | --- | ---: |
| R1 | A mixed-case secret produces uppercase-normalized game behavior. | 2.7 |
| R2 | The configured incorrect-guess limit is stored and honored. | 2.7 |
| R3 | A newly constructed game is in progress. | 2.7 |
| R4 | A valid correct letter is accepted and returns a caller-visible result. | 2.7 |
| R5 | An invalid character returns `INVALID` without changing state or incorrect history. | 2.7 |
| R6 | An absent valid letter returns `INCORRECT` and is recorded exactly once. | 2.7 |
| R7 | Repeating a correct or incorrect letter returns `DUPLICATE` without mutation. | 2.7 |
| R8 | Guessing all unique letters transitions the game to `WON`. | 2.7 |
| R9 | Reaching exactly the incorrect-guess limit transitions the game to `LOST`. | 2.7 |
| R10 | Before either terminal condition, the game remains `IN_PROGRESS`. | 2.7 |

## Public and error contract — 3 points

Each contract check is binary and worth one point:

1. A public Hangman entry type can be constructed with a secret and limit.
2. A public single-letter guess operation returns caller-visible, distinguishable
   correct, incorrect, invalid, and duplicate outcomes.
3. Game state is caller-visible and invalid input returns an invalid result
   rather than throwing an exception.

## Common acceptance scenarios

The harness runs the same scenario sequence for every adapter:

1. Construct `BaNaNa` with limit 3. Confirm initial progress; guess `A`;
   repeat `A` and require duplicate without mutation; guess `B` and `N`; require
   the final state to be won.
2. Construct `JAVA` with limit 3. Guess `1` and require invalid without
   mutation; guess `Z` and require incorrect with one `Z` in history; repeat
   `Z` and require duplicate with unchanged history.
3. Construct `A` with limit 2. Guess `X` and require in progress; guess `Y`
   and require lost exactly at the limit. Repeat with limit 1 and require loss
   after the first incorrect guess.

## Fixed 100-point scorecard

### Seed4J effectiveness — 35 points

- Discovery and help — 5: version evidence 1, global help or catalog 2,
  relevant module help before selection 2.
- Preflight and plan — 8: read-only plan before apply 2,
  dependency/provider/path evaluation 2, parameters and sources evaluation 2,
  Git mutation-state evaluation 2.
- Module choice and order — 8: specification fit 4, dependency-safe order 2,
  economical selection without unrelated modules 2.
- Explicit parameters — 7: required parameters 4, reproducibility-relevant
  parameters 3.
- Reproducible history and wrapper — 7: `.seed4j/modules` records 2, coherent
  module commits 2, appropriate usable build wrapper 3.

Missing evidence loses only the predefined subpoints. A successful build does
not substitute for discovery or planning evidence.

### Specification correctness — 30 points

- R1–R10 — 27 points, allocated as defined above.
- Public and error contract — 3 points, allocated as defined above.

### Test quality — 20 points

- Native verification — 6 points, awarded only when the complete native command
  succeeds at the pinned implementation.
- Requirement coverage — 8 points, 0.8 for each R1–R10 that has a meaningful
  native behavior-facing test assertion.
- Boundary and failure coverage — 3 points: invalid input 1, both correct and
  incorrect duplicate paths 1, before/at-loss threshold boundary 1.
- Enforced coverage gate — 3 points, awarded only for a meaningful automated
  threshold enforced by the build. A generated report alone receives zero.

Raw test count is not scored.

### Design and reproducibility — 15 points

- Separation and clarity — 6 points: cohesive responsibilities 2,
  intention-revealing state/result names 2, no redundant synchronized state 2.
- Boundary robustness — 4 points: invalid stability 1, duplicate stability 1,
  exact threshold transition 1, case and repeated-letter interaction 1.
- Minimal public state/API — 3 points: no mutable public fields 1, immutable or
  defensive collection exposure 1, no unrelated public production surface 1.
- Conventional layout — 2 points: standard source/test layout 1, conventional
  build metadata and wrapper 1.

## Immutable inspection procedure

For each run, create a distinct directory with `mktemp -d` and extract only its
pinned implementation with:

```sh
git archive <implementation-commit> | tar -x -C <temporary-directory>
```

Inspect the pinned transcript, Seed4J module records, build files, sources,
tests, wrapper, and commit sequence. Use a committed executable wrapper for
native verification when present; otherwise use the repository's declared
native command without installing tools or generating a wrapper. Never modify
an implementation branch or repair a result.

## Evidence and comparison rules

Every deduction must cite an immutable artifact, transcript record, command, or
acceptance observation. Timing, source and test size, test count, transcript
size, and token or cost availability are unweighted. Missing data is reported
as unavailable, never as zero.

Runner status and evaluator observations remain distinct. The report will show
same-effort model comparison before the aggregate scorecard, preserve exact
ties, and avoid causal claims about Seed4J without a control run.

## Results

Result evidence, deductions, scores, comparisons, limitations, and reproduction
instructions will be added after this protocol commit is frozen.
