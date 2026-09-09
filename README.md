# Seed4J CLI Hangman model evaluation

This repository is a controlled, descriptive comparison of three Codex models
implementing the same [Hangman specification](https://github.com/renanfranca/seed4j-cli-hangman-kata/blob/340804336f01a8cd957d6522c5ee6cdb7b15c7df/SPEC.md)
with the [Seed4J CLI](https://github.com/seed4j/seed4j-cli). It compares observed
outputs; with one run per model and no no-Seed4J control, it does not establish
that Seed4J or a model caused the differences.

The exact English prompt was:

```text
Implement the specification in SPEC.md using the already-installed Seed4J CLI tool as support.
```

## Controlled matrix

All runs used `xhigh` effort, Seed4J CLI 0.0.4, Seed4J runtime 2.2.0 in standard
mode, the same specification and prompt hashes, and the same
[`8a3c3b5cfc1f520d3ab4d069ddc219de724c6e9e`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/8a3c3b5cfc1f520d3ab4d069ddc219de724c6e9e)
base. Each implementation ran on a separate branch.

| Index | Model | Effort | Living branch | Pinned implementation | Pinned audit |
| ---: | --- | --- | --- | --- | --- |
| 1 | `gpt-5.6-luna` | `xhigh` | [`hangman-kata-luna-xhigh`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/hangman-kata-luna-xhigh) | [`9613f8eeaa5d1375c9e595ea5ab44ce63b0896dc`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/9613f8eeaa5d1375c9e595ea5ab44ce63b0896dc) | [`6f8722d55a1e9fd0f5be9842e689db3014cdaec7`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/6f8722d55a1e9fd0f5be9842e689db3014cdaec7) |
| 2 | `gpt-5.6-terra` | `xhigh` | [`hangman-kata-terra-xhigh`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/hangman-kata-terra-xhigh) | [`f463cb061ade07c1adf060664f8da718338664a4`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/f463cb061ade07c1adf060664f8da718338664a4) | [`ca749c49bf81b0230ef600817ca1b7b37c862c70`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/ca749c49bf81b0230ef600817ca1b7b37c862c70) |
| 3 | `gpt-5.6-sol` | `xhigh` | [`hangman-kata-sol-xhigh`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/hangman-kata-sol-xhigh) | [`ea7bda4e64df04ae9a3058e6878edb79efe0eb21`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/ea7bda4e64df04ae9a3058e6878edb79efe0eb21) | [`afe186dbc40d8bae47f31f501490c53b75854cfb`](https://github.com/renanfranca/seed4j-cli-hangman-kata/tree/afe186dbc40d8bae47f31f501490c53b75854cfb) |

## Principal findings

All three implementations pass the ten frozen functional requirements and
three public/error-contract checks. Terra ranks first at 97/100, Sol second at
95/100, and Luna third at 84/100. Luna's runner failure remains distinct from
the evaluator result: its public API passes the common harness, but native
`mvn verify` cannot start because the implementation has no wrapper and this
host has no system Maven. The experiment permits same-effort model comparison;
same-model effort comparison is unavailable.

See the complete [methodology, evidence, scoring, limitations, and reproduction](MODEL_EVALUATION.md).

## Reproduce

Prerequisites are Java 25+ and Node.js 22+. For the pinned environment:

```sh
npm install -g seed4j-cli@0.0.4
seed4j skill install
```

For the latest unpinned CLI instead:

```sh
npm install -g seed4j-cli
seed4j skill install
```

Create a distinct branch from the pinned common base for each model run, keep
the implementation branches isolated, use the exact prompt above, and validate
each result with its committed wrapper when present. The full report gives the
immutable extraction and common Java 25 harness procedure. See the
[authoritative Seed4J CLI repository](https://github.com/seed4j/seed4j-cli) and
[official OpenAI skills documentation](https://developers.openai.com/codex/skills).

This evaluation is licensed under the [Apache License 2.0](LICENSE).
