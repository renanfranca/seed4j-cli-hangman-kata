package com.github.renanfranca.hangman;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HangmanTest {

  @Test
  void startsInProgressWithANormalizedSecretWord() {
    Hangman game = new Hangman("balloon", 3);

    assertThat(game.secretWord()).isEqualTo("BALLOON");
    assertThat(game.maximumIncorrectGuesses()).isEqualTo(3);
    assertThat(game.state()).isEqualTo(GameState.IN_PROGRESS);
    assertThat(game.isInProgress()).isTrue();
    assertThat(game.maskedWord()).isEqualTo("_______");
    assertThat(game.incorrectGuesses()).isEmpty();
    assertThat(game.remainingGuesses()).isEqualTo(3);
  }

  @Test
  void aCorrectGuessIsCaseInsensitiveAndRevealsEveryOccurrence() {
    Hangman game = new Hangman("balloon", 3);

    assertThat(game.guess('l')).isEqualTo(GuessResult.CORRECT);

    assertThat(game.maskedWord()).isEqualTo("__LL___");
    assertThat(game.incorrectGuesses()).isEmpty();
    assertThat(game.remainingGuesses()).isEqualTo(3);
    assertThat(game.state()).isEqualTo(GameState.IN_PROGRESS);
  }

  @Test
  void anIncorrectGuessIsRememberedAndConsumesAnAttempt() {
    Hangman game = new Hangman("CAT", 3);

    assertThat(game.guess('x')).isEqualTo(GuessResult.INCORRECT);
    assertThat(game.guess('z')).isEqualTo(GuessResult.INCORRECT);

    assertThat(game.incorrectGuesses()).containsExactly('X', 'Z');
    assertThat(game.remainingGuesses()).isEqualTo(1);
  }

  @Test
  void aPreviouslyCorrectOrIncorrectGuessIsADuplicate() {
    Hangman game = new Hangman("CAT", 3);

    assertThat(game.guess('c')).isEqualTo(GuessResult.CORRECT);
    assertThat(game.guess('C')).isEqualTo(GuessResult.DUPLICATE);
    assertThat(game.guess('x')).isEqualTo(GuessResult.INCORRECT);
    assertThat(game.guess('X')).isEqualTo(GuessResult.DUPLICATE);

    assertThat(game.maskedWord()).isEqualTo("C__");
    assertThat(game.incorrectGuesses()).containsExactly('X');
    assertThat(game.remainingGuesses()).isEqualTo(2);
  }

  @ParameterizedTest
  @ValueSource(chars = { ' ', '-', '1', '?', 'é' })
  void invalidCharactersDoNotChangeTheGame(char invalidCharacter) {
    Hangman game = new Hangman("CAT", 3);

    assertThat(game.guess(invalidCharacter)).isEqualTo(GuessResult.INVALID);

    assertThat(game.maskedWord()).isEqualTo("___");
    assertThat(game.incorrectGuesses()).isEmpty();
    assertThat(game.remainingGuesses()).isEqualTo(3);
    assertThat(game.state()).isEqualTo(GameState.IN_PROGRESS);
  }

  @Test
  void guessingEveryDistinctLetterWinsTheGame() {
    Hangman game = new Hangman("ABA", 2);

    assertThat(game.guess('a')).isEqualTo(GuessResult.CORRECT);
    assertThat(game.state()).isEqualTo(GameState.IN_PROGRESS);
    assertThat(game.guess('b')).isEqualTo(GuessResult.CORRECT);

    assertThat(game.maskedWord()).isEqualTo("ABA");
    assertThat(game.state()).isEqualTo(GameState.WON);
    assertThat(game.isInProgress()).isFalse();
    assertThat(game.remainingGuesses()).isEqualTo(2);
  }

  @Test
  void reachingTheIncorrectGuessLimitLosesTheGame() {
    Hangman game = new Hangman("CAT", 2);

    assertThat(game.guess('x')).isEqualTo(GuessResult.INCORRECT);
    assertThat(game.state()).isEqualTo(GameState.IN_PROGRESS);
    assertThat(game.remainingGuesses()).isEqualTo(1);
    assertThat(game.guess('z')).isEqualTo(GuessResult.INCORRECT);

    assertThat(game.state()).isEqualTo(GameState.LOST);
    assertThat(game.isInProgress()).isFalse();
    assertThat(game.remainingGuesses()).isZero();
  }

  @Test
  void guessesAreRejectedAfterTheGameFinishes() {
    Hangman wonGame = new Hangman("A", 1);
    wonGame.guess('a');

    Hangman lostGame = new Hangman("A", 1);
    lostGame.guess('x');

    assertThatIllegalStateException().isThrownBy(() -> wonGame.guess('b'));
    assertThatIllegalStateException().isThrownBy(() -> lostGame.guess('b'));
  }

  @Test
  void incorrectGuessesCannotBeChangedByCallers() {
    Hangman game = new Hangman("CAT", 2);
    game.guess('x');

    List<Character> incorrectGuesses = game.incorrectGuesses();

    assertThat(incorrectGuesses).containsExactly('X');
    assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> incorrectGuesses.add('Z'));
  }

  @Test
  void rejectsInvalidConstructionArguments() {
    assertThatIllegalArgumentException().isThrownBy(() -> new Hangman(null, 3));
    assertThatIllegalArgumentException().isThrownBy(() -> new Hangman("", 3));
    assertThatIllegalArgumentException().isThrownBy(() -> new Hangman("two words", 3));
    assertThatIllegalArgumentException().isThrownBy(() -> new Hangman("WORD", 0));
    assertThatIllegalArgumentException().isThrownBy(() -> new Hangman("WORD", -1));
  }
}
