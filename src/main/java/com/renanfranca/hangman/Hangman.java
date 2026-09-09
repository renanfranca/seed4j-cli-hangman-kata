package com.renanfranca.hangman;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A game of Hangman using ASCII letters from A to Z.
 *
 * <p>Words and guesses are normalized to uppercase. A game can only be changed while it is
 * {@link GameState#IN_PROGRESS}.
 */
public final class Hangman {

    private final String secretWord;
    private final int maxIncorrectGuesses;
    private final Set<Character> correctGuesses = new LinkedHashSet<>();
    private final Set<Character> incorrectGuesses = new LinkedHashSet<>();
    private GameState gameState = GameState.IN_PROGRESS;

    /**
     * Creates a game with a secret ASCII word and the number of incorrect guesses that lose it.
     *
     * @param secretWord the word to reveal, containing only letters A through Z
     * @param maxIncorrectGuesses the positive number of incorrect guesses allowed
     */
    public Hangman(String secretWord, int maxIncorrectGuesses) {
        if (secretWord == null || secretWord.isBlank() || !containsOnlyAsciiLetters(secretWord)) {
            throw new IllegalArgumentException("secretWord must contain at least one letter from A to Z");
        }
        if (maxIncorrectGuesses <= 0) {
            throw new IllegalArgumentException("maxIncorrectGuesses must be positive");
        }

        this.secretWord = secretWord.toUpperCase(Locale.ROOT);
        this.maxIncorrectGuesses = maxIncorrectGuesses;
    }

    /**
     * Makes a one-letter guess.
     *
     * @param letter an ASCII letter, case-insensitive
     * @return the outcome of the guess
     */
    public GuessResult guess(char letter) {
        if (gameState != GameState.IN_PROGRESS) {
            return GuessResult.GAME_OVER;
        }

        if (!isAsciiLetter(letter)) {
            return GuessResult.INVALID;
        }

        var normalizedLetter = Character.toUpperCase(letter);
        if (correctGuesses.contains(normalizedLetter) || incorrectGuesses.contains(normalizedLetter)) {
            return GuessResult.DUPLICATE;
        }

        if (secretWord.indexOf(normalizedLetter) >= 0) {
            correctGuesses.add(normalizedLetter);
            updateGameState();
            return GuessResult.CORRECT;
        }

        incorrectGuesses.add(normalizedLetter);
        updateGameState();
        return GuessResult.INCORRECT;
    }

    /**
     * Makes a one-letter guess supplied as text.
     *
     * @param letter exactly one ASCII letter, case-insensitive
     * @return {@link GuessResult#INVALID} unless {@code letter} contains exactly one valid letter
     */
    public GuessResult guess(String letter) {
        if (letter == null || letter.length() != 1) {
            return GuessResult.INVALID;
        }
        return guess(letter.charAt(0));
    }

    public String getSecretWord() {
        return secretWord;
    }

    public int getMaxIncorrectGuesses() {
        return maxIncorrectGuesses;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isInProgress() {
        return gameState == GameState.IN_PROGRESS;
    }

    public boolean hasWon() {
        return gameState == GameState.WON;
    }

    public boolean hasLost() {
        return gameState == GameState.LOST;
    }

    /**
     * Returns the secret word with unguessed letters replaced by underscores.
     */
    public String getMaskedWord() {
        var maskedWord = new StringBuilder(secretWord.length());
        for (var index = 0; index < secretWord.length(); index++) {
            var letter = secretWord.charAt(index);
            maskedWord.append(correctGuesses.contains(letter) ? letter : '_');
        }
        return maskedWord.toString();
    }

    /**
     * Returns incorrect guesses in the order they were made.
     */
    public List<Character> getIncorrectGuesses() {
        return List.copyOf(incorrectGuesses);
    }

    /**
     * Returns correct guesses in the order they were made.
     */
    public List<Character> getCorrectGuesses() {
        return List.copyOf(correctGuesses);
    }

    public int getIncorrectGuessCount() {
        return incorrectGuesses.size();
    }

    public int getRemainingGuesses() {
        return maxIncorrectGuesses - incorrectGuesses.size();
    }

    private void updateGameState() {
        if (secretWord.chars().allMatch(letter -> correctGuesses.contains((char) letter))) {
            gameState = GameState.WON;
        } else if (incorrectGuesses.size() == maxIncorrectGuesses) {
            gameState = GameState.LOST;
        }
    }

    private static boolean containsOnlyAsciiLetters(String text) {
        return text.chars().allMatch(letter -> isAsciiLetter((char) letter));
    }

    private static boolean isAsciiLetter(char letter) {
        return (letter >= 'A' && letter <= 'Z') || (letter >= 'a' && letter <= 'z');
    }
}
