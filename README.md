# Hangman Kata

A small Java library implementing the Hangman game rules in [SPEC.md](SPEC.md).

## Requirements

- Java 25

## Test

```bash
./mvnw verify
```

## Usage

```java
var game = new Hangman("puzzle", 3);

game.getMaskedWord(); // "______"
game.guess('z');      // GuessResult.CORRECT
game.getMaskedWord(); // "__ZZ__"
game.guess('x');      // GuessResult.INCORRECT
game.getRemainingGuesses(); // 2
```

`Hangman` accepts ASCII letters (`A`–`Z`) and normalizes words and guesses to uppercase. Use
`getGameState()` to check for `IN_PROGRESS`, `WON`, or `LOST`; incorrect guesses and masked-word
state are available through `getIncorrectGuesses()` and `getMaskedWord()`.
