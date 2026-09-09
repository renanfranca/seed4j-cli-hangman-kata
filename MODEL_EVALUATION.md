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

The protocol above was frozen in commit
[`eef767abbb99510fdfaa6ae3f121b19a66a0cfcd`](https://github.com/renanfranca/seed4j-cli-hangman-kata/commit/eef767abbb99510fdfaa6ae3f121b19a66a0cfcd)
before any scoring inspection. All three public implementations pass every
frozen functional and contract check. Terra ranks first at 97, Sol second at
95, and Luna third at 84. The separation comes from Seed4J economy and wrapper
use, native verification, and design—not from the common acceptance behavior.

## Methodology execution

The evaluator pinned each audit head and its manifest-declared implementation
commit, checked that the objects exist, and extracted each implementation into
a different `mktemp -d` directory with `git archive`. No implementation branch
was checked out, amended, or pushed during evaluation. The live branches below
are navigation aids; all evidence links use complete immutable commit IDs.
The evaluation branch itself was created directly from immutable
[`origin/main`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/340804336f01a8cd957d6522c5ee6cdb7b15c7df)
after confirming the branch name was absent locally and remotely.

| Index | Live branch | Immutable run manifest | Immutable transcript | Immutable implementation |
| ---: | --- | --- | --- | --- |
| 1 | [`hangman-kata-luna-xhigh`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/hangman-kata-luna-xhigh) | [`run.json`](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/6f8722d55a1e9fd0f5be9842e689db3014cdaec7/.seed4j-evaluation/run.json) | [`CONVERSATION_TRANSCRIPT.md`](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/6f8722d55a1e9fd0f5be9842e689db3014cdaec7/CONVERSATION_TRANSCRIPT.md) | [`9613f8eeaa5d1375c9e595ea5ab44ce63b0896dc`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/9613f8eeaa5d1375c9e595ea5ab44ce63b0896dc) |
| 2 | [`hangman-kata-terra-xhigh`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/hangman-kata-terra-xhigh) | [`run.json`](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/ca749c49bf81b0230ef600817ca1b7b37c862c70/.seed4j-evaluation/run.json) | [`CONVERSATION_TRANSCRIPT.md`](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/ca749c49bf81b0230ef600817ca1b7b37c862c70/CONVERSATION_TRANSCRIPT.md) | [`f463cb061ade07c1adf060664f8da718338664a4`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/f463cb061ade07c1adf060664f8da718338664a4) |
| 3 | [`hangman-kata-sol-xhigh`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/hangman-kata-sol-xhigh) | [`run.json`](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/afe186dbc40d8bae47f31f501490c53b75854cfb/.seed4j-evaluation/run.json) | [`CONVERSATION_TRANSCRIPT.md`](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/afe186dbc40d8bae47f31f501490c53b75854cfb/CONVERSATION_TRANSCRIPT.md) | [`ea7bda4e64df04ae9a3058e6878edb79efe0eb21`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/ea7bda4e64df04ae9a3058e6878edb79efe0eb21) |

Native verification used `mvn verify` for Luna because its repository has no
wrapper, and `./mvnw verify` for Terra and Sol. Separately, one identical Java
25 assertion harness and three thin adapters were created only in the temporary
directories. Production sources plus adapter and harness were compiled with
`javac --release 25`, then run with `java -ea`. The adapters only constructed
the public Hangman type and renamed public result, state, and incorrect-history
observations; they used no reflection, private state, bookkeeping, or game
logic.

## Acceptance results

Every requirement was recorded independently. `PASS` means the common harness
observed the requirement through public APIs at the pinned implementation.

| ID | Luna | Terra | Sol |
| --- | :---: | :---: | :---: |
| R1 mixed-case secret normalization | PASS | PASS | PASS |
| R2 configured limit honored | PASS | PASS | PASS |
| R3 initial progress | PASS | PASS | PASS |
| R4 correct result | PASS | PASS | PASS |
| R5 invalid result without mutation | PASS | PASS | PASS |
| R6 incorrect result recorded once | PASS | PASS | PASS |
| R7 both duplicate paths without mutation | PASS | PASS | PASS |
| R8 all unique letters wins | PASS | PASS | PASS |
| R9 loss exactly at limit | PASS | PASS | PASS |
| R10 progress before terminal condition | PASS | PASS | PASS |
| C1 public construction with secret and limit | PASS | PASS | PASS |
| C2 public single-letter operation with distinct results | PASS | PASS | PASS |
| C3 visible state and invalid returned, not thrown | PASS | PASS | PASS |

The `BaNaNa`, `JAVA`, and limit-one/limit-two paths completed successfully for
all runs. This gives each implementation 27/27 functional points and 3/3
contract points. The executable observation agrees with the immutable public
APIs for [Luna](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/9613f8eeaa5d1375c9e595ea5ab44ce63b0896dc/src/main/java/com/example/hangman/Hangman.java),
[Terra](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/f463cb061ade07c1adf060664f8da718338664a4/src/main/java/com/renanfranca/hangman/Hangman.java),
and [Sol](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/ea7bda4e64df04ae9a3058e6878edb79efe0eb21/src/main/java/com/github/renanfranca/hangman/Hangman.java).

## Frozen scoring table

| Run | Seed4J /35 | Specification /30 | Tests /20 | Design /15 | Total /100 |
| --- | ---: | ---: | ---: | ---: | ---: |
| Luna, index 1 | 32 | 30 | 11 | 11 | **84** |
| Terra, index 2 | 35 | 30 | 17 | 15 | **97** |
| Sol, index 3 | 33 | 30 | 17 | 15 | **95** |

### Seed4J effectiveness

| Run | Discovery /5 | Preflight /8 | Choice/order /8 | Parameters /7 | History/wrapper /7 | Total |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| Luna | 5 | 8 | 8 | 7 | 4 | 32 |
| Terra | 5 | 8 | 8 | 7 | 7 | 35 |
| Sol | 5 | 8 | 6 | 7 | 7 | 33 |

All runs captured `seed4j --version`, `seed4j list`, and `seed4j --help` before
selection; each reported CLI 0.0.4, runtime 2.2.0, and standard mode. The exact
mutating workflow recorded by the transcripts was:

- Luna inspected `apply-set`, `init`, `maven-java`, and `java-base` help, then
  planned and repeated without `--plan`:

  ```sh
  seed4j apply-set init maven-java --base-name hangman \
    --project-name 'Hangman Kata' --package-name com.example.hangman \
    --project-path . --node-package-manager npm --end-of-line lf \
    --indent-size 4 --plan
  seed4j apply-set init maven-java --base-name hangman \
    --project-name 'Hangman Kata' --package-name com.example.hangman \
    --project-path . --node-package-manager npm --end-of-line lf \
    --indent-size 4
  ```

- Terra inspected `apply-set`, `init`, `maven-java`, and `approval-tests` help.
  Its first plan requested only `maven-java` and exposed the pending `init`
  dependency:

  ```sh
  seed4j apply-set maven-java --plan --project-path "$PWD" \
    --base-name hangman --project-name 'Hangman Kata' \
    --package-name com.renanfranca.hangman --node-package-manager npm \
    --end-of-line lf --indent-size 4
  ```

  It then planned and applied the dependency-complete set. After inspecting
  wrapper help, it planned and applied the wrapper separately:

  ```sh
  seed4j apply-set init maven-java --plan --project-path "$PWD" \
    --base-name hangman --project-name 'Hangman Kata' \
    --package-name com.renanfranca.hangman --node-package-manager npm \
    --end-of-line lf --indent-size 4
  seed4j apply-set init maven-java --project-path "$PWD" \
    --base-name hangman --project-name 'Hangman Kata' \
    --package-name com.renanfranca.hangman --node-package-manager npm \
    --end-of-line lf --indent-size 4
  seed4j apply maven-wrapper --plan --project-path "$PWD"
  seed4j apply maven-wrapper --project-path "$PWD"
  ```

- Sol inspected `apply-set`, `init`, `maven-java`, `maven-wrapper`, and
  `approval-tests` help, then planned and repeated without `--plan`:

  ```sh
  seed4j apply-set init maven-java maven-wrapper approval-tests --plan \
    --project-path . --project-name 'Hangman Kata' \
    --base-name hangmanKata --package-name com.github.renanfranca.hangman \
    --node-package-manager npm --end-of-line lf --indent-size 2
  seed4j apply-set init maven-java maven-wrapper approval-tests \
    --project-path . --project-name 'Hangman Kata' \
    --base-name hangmanKata --package-name com.github.renanfranca.hangman \
    --node-package-manager npm --end-of-line lf --indent-size 2
  ```

The plan rendered Sol's dependency-safe execution order as `init`,
`maven-java`, `approval-tests`, `maven-wrapper`. Each run verified it was on its
pre-created isolated result branch, kept Seed4J's default one-commit-per-module
mode, made one separate `feat: implement Hangman Kata` commit, and pushed only
its own branch. The exact parameter sources, plan output, writability checks,
Git status, commit history, and delivery decision remain in the three immutable
transcripts linked above.

- Luna recorded the CLI/runtime versions, catalog, global and selected-module
  help, Git writability, a valid parameter-resolved plan, and the matching
  `init maven-java` apply. It earned both history subchecks through two module
  records and two coherent module commits, but lost the predefined three
  wrapper points because no wrapper was selected or committed. See the
  [transcript](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/6f8722d55a1e9fd0f5be9842e689db3014cdaec7/CONVERSATION_TRANSCRIPT.md),
  [module records](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/9613f8eeaa5d1375c9e595ea5ab44ce63b0896dc/.seed4j/modules),
  and generated [Maven build](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/9613f8eeaa5d1375c9e595ea5ab44ce63b0896dc/pom.xml).
- Terra first planned `maven-java` alone, observed the missing `init` provider,
  then planned and applied `init maven-java` with every parameter explicit.
  After finding system Maven unavailable, it inspected, planned, and applied
  `maven-wrapper`. The three records, three module commits, and committed
  wrapper earn full reproducibility credit. See the
  [transcript](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/ca749c49bf81b0230ef600817ca1b7b37c862c70/CONVERSATION_TRANSCRIPT.md),
  [module records](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/f463cb061ade07c1adf060664f8da718338664a4/.seed4j/modules),
  and [`mvnw`](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/f463cb061ade07c1adf060664f8da718338664a4/mvnw).
- Sol inspected all four selected modules, planned before mutation, explicitly
  evaluated resolved dependencies, execution order, parameter sources, Git
  writability, and then applied the same validated set. It lost only the
  two-point economy subcheck: `approval-tests` added a dependency, generated
  documentation, and a test-side settings class, but `HangmanTest` contains no
  approval-based test; only the generated, suppressed-as-unused settings class
  references the library. See the
  [transcript](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/afe186dbc40d8bae47f31f501490c53b75854cfb/CONVERSATION_TRANSCRIPT.md),
  [four module records](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/ea7bda4e64df04ae9a3058e6878edb79efe0eb21/.seed4j/modules),
  [`pom.xml`](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/ea7bda4e64df04ae9a3058e6878edb79efe0eb21/pom.xml),
  and the final [native tests](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/ea7bda4e64df04ae9a3058e6878edb79efe0eb21/src/test/java/com/github/renanfranca/hangman/HangmanTest.java).

The immutable module commit sequences are:

- Luna: [`init`](https://github.com/renanfranca/seed4j-cli-hangman-kata/commit/93f4599e7ee529150d8b18c3ad0a9803b13bede1),
  [`maven-java`](https://github.com/renanfranca/seed4j-cli-hangman-kata/commit/03720d2451ee34357c40f544dd32ebe1ec329219).
- Terra: [`init`](https://github.com/renanfranca/seed4j-cli-hangman-kata/commit/23dd14c61f52dac3ef0770d8bbac52f03c844796),
  [`maven-java`](https://github.com/renanfranca/seed4j-cli-hangman-kata/commit/2d506d8bcb2130e84e63db820e8a0d1619659bca),
  [`maven-wrapper`](https://github.com/renanfranca/seed4j-cli-hangman-kata/commit/4296c65dd24b974ea6bc1e58fd6ccd4386e6d744).
- Sol: [`init`](https://github.com/renanfranca/seed4j-cli-hangman-kata/commit/ca8a02e07fc928077dc4c5be50d6104767fdf916),
  [`maven-java`](https://github.com/renanfranca/seed4j-cli-hangman-kata/commit/2d772feacc04992ea1672acff48a2267e2200544),
  [`approval-tests`](https://github.com/renanfranca/seed4j-cli-hangman-kata/commit/78d757349e9053b638cd01a8f0cd1d4a03f57a19),
  [`maven-wrapper`](https://github.com/renanfranca/seed4j-cli-hangman-kata/commit/7691581c9fe0114fadcfb005d8ed05fe8783b51f).

### Test quality

| Run | Native /6 | R1–R10 /8 | Invalid/duplicates/threshold /3 | Coverage gate /3 | Total |
| --- | ---: | ---: | ---: | ---: | ---: |
| Luna | 0 | 8 | 3 | 0 | 11 |
| Terra | 6 | 8 | 3 | 0 | 17 |
| Sol | 6 | 8 | 3 | 0 | 17 |

The native test sources meaningfully assert all ten frozen behaviors and all
three boundary/failure groups in every run: [Luna tests](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/9613f8eeaa5d1375c9e595ea5ab44ce63b0896dc/src/test/java/com/example/hangman/HangmanTest.java),
[Terra tests](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/f463cb061ade07c1adf060664f8da718338664a4/src/test/java/com/renanfranca/hangman/HangmanTest.java),
and [Sol tests](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/ea7bda4e64df04ae9a3058e6878edb79efe0eb21/src/test/java/com/github/renanfranca/hangman/HangmanTest.java).
No pinned `pom.xml` enforces a coverage threshold, so all lose the fixed three
coverage-gate points.

Luna loses six native-verification points because its committed repository has
no wrapper and `mvn verify` exits 127 on this host: `mvn` is unavailable. The
runner manifest records the same failure even though its transcript also shows
a non-native cached-classpath JUnit execution with 10 passing tests. Terra's
committed wrapper passes `verify` with 8 tests; Sol's passes with 14. These
runner records are immutable for [Luna](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/6f8722d55a1e9fd0f5be9842e689db3014cdaec7/.seed4j-evaluation/run.json),
[Terra](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/ca749c49bf81b0230ef600817ca1b7b37c862c70/.seed4j-evaluation/run.json),
and [Sol](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/afe186dbc40d8bae47f31f501490c53b75854cfb/.seed4j-evaluation/run.json).

### Design and reproducibility

| Run | Separation /6 | Robustness /4 | Public state/API /3 | Layout/build /2 | Total |
| --- | ---: | ---: | ---: | ---: | ---: |
| Luna | 4 | 4 | 2 | 1 | 11 |
| Terra | 6 | 4 | 3 | 2 | 15 |
| Sol | 6 | 4 | 3 | 2 | 15 |

All implementations have intention-revealing state/result types, stable
invalid/duplicate/threshold behavior, no mutable public fields, and read-only
incorrect-guess collections. Terra and Sol use disjoint correct and incorrect
sets and return immutable snapshots. Luna loses two points because its
`guessedLetters` set duplicates incorrect membership held in
`incorrectGuesses`, creating synchronized representations of the same fact. It
also loses the minimal-surface point because its production type publishes
redundant Java/Pascal-style aliases and multiple equivalent state/count
accessors, and one layout/build point because no wrapper is committed. Compare
the pinned sources for [Luna](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/9613f8eeaa5d1375c9e595ea5ab44ce63b0896dc/src/main/java/com/example/hangman/Hangman.java),
[Terra](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/f463cb061ade07c1adf060664f8da718338664a4/src/main/java/com/renanfranca/hangman/Hangman.java),
and [Sol](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/ea7bda4e64df04ae9a3058e6878edb79efe0eb21/src/main/java/com/github/renanfranca/hangman/Hangman.java).

## Same-effort comparison

The controlled comparison is Luna versus Terra versus Sol at the common
`xhigh` effort. They share the same English prompt, specification hash, base
commit, Seed4J CLI/runtime, and Seed4J skill-tree hash, as recorded in each
immutable manifest. All tie on public acceptance and native requirement
coverage. Terra leads because it combines complete Seed4J discipline with a
usable wrapper; Sol is two points behind because its ApprovalTests module was
unused by the behavior tests and therefore not economical; Luna trails because
it has no wrapper, cannot run the native build on the host, and has the two
design deductions described above. A same-model effort comparison is
unavailable because each model was run at only `xhigh`.

These are associations within three observed executions, not evidence that a
model or Seed4J caused the differences. There is one run per model and no
no-Seed4J control.

## Aggregate ranking

| Rank | Index | Model | Effort | Score |
| ---: | ---: | --- | --- | ---: |
| 1 | 2 | `gpt-5.6-terra` | `xhigh` | **97** |
| 2 | 3 | `gpt-5.6-sol` | `xhigh` | **95** |
| 3 | 1 | `gpt-5.6-luna` | `xhigh` | **84** |

No aggregate ties occurred. Had there been a tie, rows would remain tied and
appear in execution-index order.

## Per-run evidence and deductions

### Luna, execution 1

- Runner status: failed, because repository-native `mvn verify` could not start
  (`mvn` absent). Evaluator acceptance: all 13 frozen checks passed.
- Seed4J invocation: planned and applied `init maven-java` with explicit
  `hangman`, `Hangman Kata`, `com.example.hangman`, `npm`, `lf`, indentation 4,
  and project path. Evidence: immutable
  [transcript](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/6f8722d55a1e9fd0f5be9842e689db3014cdaec7/CONVERSATION_TRANSCRIPT.md).
- Deductions: no wrapper (3 Seed4J, 6 native verification, 1 build/layout); no
  enforced coverage threshold (3); synchronized guess representations (2);
  redundant public aliases/accessors (1). Total deductions: 16.

### Terra, execution 2

- Runner status and evaluator status: passed. The pinned wrapper executed 8
  tests with no failures; all 13 frozen acceptance checks passed.
- Seed4J invocation: resolved an initially pending provider, then planned and
  applied `init maven-java`; separately inspected, planned, and applied
  `maven-wrapper`. All project parameters were explicit. Evidence: immutable
  [transcript](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/ca749c49bf81b0230ef600817ca1b7b37c862c70/CONVERSATION_TRANSCRIPT.md).
- Deduction: no enforced coverage threshold (3). Total deductions: 3.

### Sol, execution 3

- Runner status and evaluator status: passed. The pinned wrapper executed 14
  tests with no failures; all 13 frozen acceptance checks passed.
- Seed4J invocation: planned `init maven-java maven-wrapper approval-tests`;
  the dependency-resolved application order was `init`, `maven-java`,
  `approval-tests`, `maven-wrapper`. All parameters were explicit. Evidence:
  immutable [transcript](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/afe186dbc40d8bae47f31f501490c53b75854cfb/CONVERSATION_TRANSCRIPT.md).
- Deductions: ApprovalTests unused by the behavior tests (2); no enforced
  coverage threshold (3). Total deductions: 5.

## Unweighted observations and metrics

| Metric | Luna | Terra | Sol |
| --- | ---: | ---: | ---: |
| Transcript-derived run duration | 8m 20s | 7m 00s | 9m 45s |
| Transcript size | 142,721 bytes | 178,070 bytes | 194,184 bytes |
| Transcript last ordinal | 287 | 249 | 269 |
| Production Java files / lines | 3 / 228 | 3 / 175 | 3 / 123 |
| Test Java files / lines | 1 / 114 | 1 / 106 | 2 / 156 |
| Native test cases observed | 10 via cached runner; native unavailable | 8 | 14 |
| Evaluator native command | exit 127; 0.02s | pass; 2.99s | pass; 2.95s |
| Acceptance compile + run | pass; 0.69s | pass; 0.67s | pass; 0.70s |
| Seed4J records / module commits | 2 / 2 | 3 / 3 | 4 / 4 |
| Token count | unavailable | unavailable | unavailable |
| Cost | unavailable | unavailable | unavailable |

Durations are wall-clock observations, not score inputs. Transcript duration is
the interval from the first recorded user message to the last recorded model
answer; transcript byte counts and last ordinals come from each pinned audit.
The second Sol test file is Seed4J-generated
[`PackageSettings.java`](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/ea7bda4e64df04ae9a3058e6878edb79efe0eb21/src/test/java/com/github/renanfranca/hangman/PackageSettings.java).

Unweighted robustness differs outside the specification. Luna accepts
punctuation inside secrets and returns `GAME_OVER` after completion. Terra also
returns `GAME_OVER`. Sol restricts secrets to English letters and throws after
completion. Sol explicitly tests immutable history snapshots; Luna returns
unmodifiable live views; Terra returns immutable snapshots. None of these
unspecified policies changes the score.

## Limitations

- The three original model executions were sequential, so later runs may have
  benefited from warmed dependency and tool caches.
- Results depend on this WSL2 host and Temurin JDK 25.0.2. Native timings are
  not portable performance measurements.
- System Maven was absent. That exposed Luna's missing wrapper and made its
  repository-native command unavailable; installing Maven was forbidden by the
  protocol.
- Transcripts were reconstructed from Codex rollout JSONL. Private or encrypted
  reasoning is intentionally excluded, so the transcript records observable
  commands and messages rather than every internal decision.
- The result branches share a public remote. Earlier branches were therefore
  discoverable by later runs, as their own transcripts show.
- This repository was public and pre-existing; prior conventions, cached
  dependencies, and platform state may influence results despite branch
  isolation.
- Token and cost data were not present in the frozen manifests or transcripts
  and are reported as unavailable.
- There is no no-Seed4J control, only one run per model, and no repeated trials.
  The experiment supports descriptive comparison, not causal attribution or
  estimates of variance.
- Only one reasoning effort was executed per model, so same-model effort
  effects cannot be evaluated.

## Reproduction instructions

### Prerequisites and installation

Use Java 25 or newer and Node.js 22 or newer. Install the exact CLI used by the
experiment for a pinned reproduction:

```sh
npm install -g seed4j-cli@0.0.4
```

To explore the current release instead, knowingly giving up version parity:

```sh
npm install -g seed4j-cli
```

Install the local agent skill with `seed4j skill install`. The authoritative
project is the [Seed4J CLI repository](https://github.com/seed4j/seed4j-cli),
and skill behavior is documented in the
[official OpenAI skills documentation](https://developers.openai.com/codex/skills).

### Recreate the model runs

1. Start from
   [`8a3c3b5cfc1f520d3ab4d069ddc219de724c6e9e`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/8a3c3b5cfc1f520d3ab4d069ddc219de724c6e9e),
   whose only change from immutable
   [`main`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/340804336f01a8cd957d6522c5ee6cdb7b15c7df)
   is the local [Seed4J CLI skill tree](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/8a3c3b5cfc1f520d3ab4d069ddc219de724c6e9e/.agents/skills/seed4j-cli).
2. Create one isolated branch per run from that exact commit. Use Luna, Terra,
   and Sol separately at `xhigh`; do not let one implementation modify another.
3. Submit the exact English prompt shown under [Immutable inputs](#immutable-inputs).
4. Pin the resulting implementation commit before evaluating. Preserve runner
   status separately from evaluator observations.

### Re-run the immutable evaluation

For each implementation SHA, use a fresh extraction:

```sh
evaluation_dir=$(mktemp -d)
git archive <implementation-sha> | tar -x -C "$evaluation_dir"
cd "$evaluation_dir"
```

Run the committed native command: `./mvnw verify` when `mvnw` exists; otherwise
run the declared `mvn verify` without installing Maven or generating a wrapper.
Create the thin adapter and the single harness described in
[Public observation surface](#public-observation-surface) only inside the
temporary directory, then compile and execute them:

```sh
classes_dir=$(mktemp -d)
javac --release 25 -d "$classes_dir" \
  $(find src/main/java evaluator -type f -name '*.java' -print | sort)
java -ea -cp "$classes_dir" AcceptanceHarness
```

Execute the three exact paths in [Common acceptance scenarios](#common-acceptance-scenarios),
record R1–R10 and C1–C3 separately, and apply the frozen scorecard without
repairing a result. Finish with `git diff --check`, verify all cited objects
with `git cat-file -e`, and confirm the evaluation branch differs from
`origin/main` only by `README.md`, `MODEL_EVALUATION.md`, and `LICENSE`.
