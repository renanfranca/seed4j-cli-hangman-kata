package com.renanfranca.hangman;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class HangmanTest {

    @Test
    void startsInProgressWithAnUppercaseWordAndAllLettersHidden() {
        var game = new Hangman("puzzle", 3);

        assertThat(game.getSecretWord()).isEqualTo("PUZZLE");
        assertThat(game.getGameState()).isEqualTo(GameState.IN_PROGRESS);
        assertThat(game.isInProgress()).isTrue();
        assertThat(game.getMaskedWord()).isEqualTo("______");
        assertThat(game.getRemainingGuesses()).isEqualTo(3);
    }

    @Test
    void correctGuessesAreCaseInsensitiveAndRevealEveryOccurrence() {
        var game = new Hangman("puzzle", 3);

        assertThat(game.guess('z')).isEqualTo(GuessResult.CORRECT);
        assertThat(game.guess('P')).isEqualTo(GuessResult.CORRECT);

        assertThat(game.getMaskedWord()).isEqualTo("P_ZZ__");
        assertThat(game.getIncorrectGuesses()).isEmpty();
        assertThat(game.getGameState()).isEqualTo(GameState.IN_PROGRESS);
    }

    @Test
    void anInvalidGuessDoesNotChangeTheGame() {
        var game = new Hangman("puzzle", 2);

        assertThat(game.guess('1')).isEqualTo(GuessResult.INVALID);
        assertThat(game.guess("zz")).isEqualTo(GuessResult.INVALID);

        assertThat(game.getMaskedWord()).isEqualTo("______");
        assertThat(game.getIncorrectGuesses()).isEmpty();
        assertThat(game.getRemainingGuesses()).isEqualTo(2);
    }

    @Test
    void anIncorrectGuessIsRecordedAndConsumesOneRemainingGuess() {
        var game = new Hangman("puzzle", 2);

        assertThat(game.guess('x')).isEqualTo(GuessResult.INCORRECT);

        assertThat(game.getIncorrectGuesses()).containsExactly('X');
        assertThat(game.getRemainingGuesses()).isEqualTo(1);
        assertThat(game.getGameState()).isEqualTo(GameState.IN_PROGRESS);
    }

    @Test
    void duplicateCorrectAndIncorrectGuessesDoNotChangeTheGame() {
        var game = new Hangman("apple", 3);

        assertThat(game.guess('a')).isEqualTo(GuessResult.CORRECT);
        assertThat(game.guess('A')).isEqualTo(GuessResult.DUPLICATE);
        assertThat(game.guess('x')).isEqualTo(GuessResult.INCORRECT);
        assertThat(game.guess('X')).isEqualTo(GuessResult.DUPLICATE);

        assertThat(game.getMaskedWord()).isEqualTo("A____");
        assertThat(game.getIncorrectGuesses()).containsExactly('X');
        assertThat(game.getRemainingGuesses()).isEqualTo(2);
    }

    @Test
    void winsWhenEveryDistinctLetterHasBeenGuessed() {
        var game = new Hangman("apple", 3);

        game.guess('a');
        game.guess('p');
        game.guess('l');
        assertThat(game.guess('e')).isEqualTo(GuessResult.CORRECT);

        assertThat(game.getMaskedWord()).isEqualTo("APPLE");
        assertThat(game.getGameState()).isEqualTo(GameState.WON);
        assertThat(game.isInProgress()).isFalse();
        assertThat(game.guess('z')).isEqualTo(GuessResult.GAME_OVER);
        assertThat(game.getIncorrectGuesses()).isEmpty();
    }

    @Test
    void losesWhenTheConfiguredNumberOfIncorrectGuessesIsReached() {
        var game = new Hangman("apple", 2);

        assertThat(game.guess('x')).isEqualTo(GuessResult.INCORRECT);
        assertThat(game.getGameState()).isEqualTo(GameState.IN_PROGRESS);
        assertThat(game.guess('y')).isEqualTo(GuessResult.INCORRECT);

        assertThat(game.getGameState()).isEqualTo(GameState.LOST);
        assertThat(game.isInProgress()).isFalse();
        assertThat(game.getRemainingGuesses()).isZero();
        assertThat(game.getIncorrectGuesses()).containsExactly('X', 'Y');
        assertThat(game.guess('z')).isEqualTo(GuessResult.GAME_OVER);
    }

    @Test
    void rejectsEmptyWordsAndNonPositiveIncorrectGuessLimits() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Hangman("", 1));
        assertThatIllegalArgumentException().isThrownBy(() -> new Hangman("word", 0));
    }
}
