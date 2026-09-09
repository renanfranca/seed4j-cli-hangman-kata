package com.github.renanfranca.hangman;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** A single game of Hangman using the English alphabet. */
public final class Hangman {

  private final String secretWord;
  private final int maximumIncorrectGuesses;
  private final Set<Character> correctGuesses = new HashSet<>();
  private final Set<Character> incorrectGuesses = new LinkedHashSet<>();
  private GameState state = GameState.IN_PROGRESS;

  public Hangman(String secretWord, int maximumIncorrectGuesses) {
    if (secretWord == null || !secretWord.matches("[A-Za-z]+")) {
      throw new IllegalArgumentException("Secret word must contain only the letters A-Z");
    }
    if (maximumIncorrectGuesses < 1) {
      throw new IllegalArgumentException("Maximum incorrect guesses must be positive");
    }

    this.secretWord = secretWord.toUpperCase(Locale.ROOT);
    this.maximumIncorrectGuesses = maximumIncorrectGuesses;
  }

  public GuessResult guess(char letter) {
    ensureGameIsInProgress();

    char normalizedLetter = Character.toUpperCase(letter);
    if (!isEnglishLetter(normalizedLetter)) {
      return GuessResult.INVALID;
    }
    if (correctGuesses.contains(normalizedLetter) || incorrectGuesses.contains(normalizedLetter)) {
      return GuessResult.DUPLICATE;
    }

    GuessResult result = recordGuess(normalizedLetter);
    recalculateState();
    return result;
  }

  public String secretWord() {
    return secretWord;
  }

  public int maximumIncorrectGuesses() {
    return maximumIncorrectGuesses;
  }

  public GameState state() {
    return state;
  }

  public boolean isInProgress() {
    return state == GameState.IN_PROGRESS;
  }

  public String maskedWord() {
    StringBuilder maskedWord = new StringBuilder(secretWord.length());
    for (char letter : secretWord.toCharArray()) {
      maskedWord.append(correctGuesses.contains(letter) ? letter : '_');
    }
    return maskedWord.toString();
  }

  public List<Character> incorrectGuesses() {
    return List.copyOf(incorrectGuesses);
  }

  public int remainingGuesses() {
    return maximumIncorrectGuesses - incorrectGuesses.size();
  }

  private GuessResult recordGuess(char letter) {
    if (secretWord.indexOf(letter) >= 0) {
      correctGuesses.add(letter);
      return GuessResult.CORRECT;
    }

    incorrectGuesses.add(letter);
    return GuessResult.INCORRECT;
  }

  private void recalculateState() {
    boolean allLettersGuessed = secretWord.chars().allMatch(letter -> correctGuesses.contains((char) letter));
    if (allLettersGuessed) {
      state = GameState.WON;
    } else if (incorrectGuesses.size() == maximumIncorrectGuesses) {
      state = GameState.LOST;
    }
  }

  private void ensureGameIsInProgress() {
    if (!isInProgress()) {
      throw new IllegalStateException("Cannot guess after the game has finished");
    }
  }

  private static boolean isEnglishLetter(char letter) {
    return letter >= 'A' && letter <= 'Z';
  }
}
