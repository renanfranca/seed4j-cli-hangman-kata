package com.example.hangman;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class HangmanTest {
    @Test
    void storesTheSecretWordInUpperCaseAndStartsInProgress() {
        Hangman game = new Hangman("banana", 5);

        assertThat(game.getSecretWord()).isEqualTo("BANANA");
        assertThat(game.getMaskedWord()).isEqualTo("______");
        assertThat(game.getState()).isEqualTo(GameState.IN_PROGRESS);
        assertThat(game.isInProgress()).isTrue();
    }

    @Test
    void correctGuessRevealsEveryOccurrence() {
        Hangman game = new Hangman("banana", 5);

        assertThat(game.guess('a')).isEqualTo(GuessResult.CORRECT);
        assertThat(game.getMaskedWord()).isEqualTo("_A_A_A");
        assertThat(game.getIncorrectGuesses()).isEmpty();
        assertThat(game.getRemainingGuesses()).isEqualTo(5);
    }

    @Test
    void incorrectGuessIsRecordedAndReducesRemainingGuesses() {
        Hangman game = new Hangman("banana", 5);

        assertThat(game.guess('z')).isEqualTo(GuessResult.INCORRECT);
        assertThat(game.getIncorrectGuesses()).containsExactly('Z');
        assertThat(game.getIncorrectGuessesCount()).isEqualTo(1);
        assertThat(game.getRemainingGuesses()).isEqualTo(4);
        assertThat(game.isInProgress()).isTrue();
    }

    @Test
    void guessesAreCaseInsensitive() {
        Hangman game = new Hangman("banana", 5);

        assertThat(game.guess('B')).isEqualTo(GuessResult.CORRECT);
        assertThat(game.guess('b')).isEqualTo(GuessResult.DUPLICATE);
        assertThat(game.getMaskedWord()).isEqualTo("B_____");
    }

    @Test
    void duplicateIncorrectGuessDoesNotConsumeAnotherAttempt() {
        Hangman game = new Hangman("banana", 2);

        assertThat(game.guess('x')).isEqualTo(GuessResult.INCORRECT);
        assertThat(game.guess('X')).isEqualTo(GuessResult.DUPLICATE);

        assertThat(game.getIncorrectGuesses()).containsExactly('X');
        assertThat(game.getRemainingGuesses()).isEqualTo(1);
        assertThat(game.isInProgress()).isTrue();
    }

    @Test
    void invalidGuessDoesNotChangeTheGame() {
        Hangman game = new Hangman("banana", 2);

        assertThat(game.guess('1')).isEqualTo(GuessResult.INVALID);
        assertThat(game.getGuessedLetters()).isEmpty();
        assertThat(game.getIncorrectGuesses()).isEmpty();
        assertThat(game.getMaskedWord()).isEqualTo("______");
        assertThat(game.isInProgress()).isTrue();
    }

    @Test
    void revealingAllLettersWinsTheGame() {
        Hangman game = new Hangman("aba", 2);

        assertThat(game.guess('a')).isEqualTo(GuessResult.CORRECT);
        assertThat(game.isInProgress()).isTrue();
        assertThat(game.guess('b')).isEqualTo(GuessResult.CORRECT);

        assertThat(game.getState()).isEqualTo(GameState.WON);
        assertThat(game.getMaskedWord()).isEqualTo("ABA");
        assertThat(game.getRemainingGuesses()).isEqualTo(2);
    }

    @Test
    void reachingTheIncorrectGuessLimitLosesTheGame() {
        Hangman game = new Hangman("cat", 2);

        assertThat(game.guess('x')).isEqualTo(GuessResult.INCORRECT);
        assertThat(game.guess('y')).isEqualTo(GuessResult.INCORRECT);

        assertThat(game.getState()).isEqualTo(GameState.LOST);
        assertThat(game.isInProgress()).isFalse();
        assertThat(game.getRemainingGuesses()).isZero();
        assertThat(game.guess('c')).isEqualTo(GuessResult.GAME_OVER);
        assertThat(game.getMaskedWord()).isEqualTo("___");
    }

    @Test
    void nonLetterSeparatorsRemainVisible() {
        Hangman game = new Hangman("ice-cream", 3);

        assertThat(game.getMaskedWord()).isEqualTo("___-_____");
        game.guess('e');
        assertThat(game.getMaskedWord()).isEqualTo("__E-__E__");
    }

    @Test
    void rejectsInvalidConstructorArguments() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Hangman(null, 3));
        assertThatIllegalArgumentException().isThrownBy(() -> new Hangman(" ", 3));
        assertThatIllegalArgumentException().isThrownBy(() -> new Hangman("word", -1));
    }
}
