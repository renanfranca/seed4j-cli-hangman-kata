package com.example.hangman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A game of Hangman.
 *
 * <p>The secret word is normalized to upper case. Guesses are limited to the
 * ASCII letters A-Z and are matched without regard to case.</p>
 */
public final class Hangman {
    private final String secretWord;
    private final int maxIncorrectGuesses;
    private final Set<Character> guessedLetters = new LinkedHashSet<>();
    private final List<Character> incorrectGuesses = new ArrayList<>();
    private GameState state;

    public Hangman(String secretWord, int maxIncorrectGuesses) {
        if (secretWord == null || secretWord.isBlank()) {
            throw new IllegalArgumentException("The secret word must not be blank");
        }
        if (maxIncorrectGuesses < 0) {
            throw new IllegalArgumentException("The incorrect guess limit must not be negative");
        }

        this.secretWord = secretWord.toUpperCase(Locale.ROOT);
        this.maxIncorrectGuesses = maxIncorrectGuesses;
        updateState();
    }

    /**
     * Attempts a letter and returns the result of that attempt.
     *
     * @param letter the guessed letter
     * @return the result of the guess
     */
    public GuessResult guess(char letter) {
        if (!isAsciiLetter(letter)) {
            return GuessResult.INVALID;
        }

        char normalizedLetter = Character.toUpperCase(letter);
        if (guessedLetters.contains(normalizedLetter)) {
            return GuessResult.DUPLICATE;
        }
        if (!isInProgress()) {
            return GuessResult.GAME_OVER;
        }

        guessedLetters.add(normalizedLetter);
        if (secretWord.indexOf(normalizedLetter) >= 0) {
            updateState();
            return GuessResult.CORRECT;
        }

        incorrectGuesses.add(normalizedLetter);
        updateState();
        return GuessResult.INCORRECT;
    }

    /** Convenience overload for clients that read guesses as one-character strings. */
    public GuessResult guess(String letter) {
        if (letter == null || letter.length() != 1) {
            return GuessResult.INVALID;
        }
        return guess(letter.charAt(0));
    }

    /** PascalCase alias for clients following the kata's original C# naming. */
    public GuessResult Guess(char letter) {
        return guess(letter);
    }

    /** PascalCase alias for clients following the kata's original C# naming. */
    public GuessResult Guess(String letter) {
        return guess(letter);
    }

    public String getSecretWord() {
        return secretWord;
    }

    /**
     * Returns the current display, keeping non-letter separators visible.
     * Unknown letters are represented by underscores.
     */
    public String getMaskedWord() {
        StringBuilder maskedWord = new StringBuilder(secretWord.length());
        for (int index = 0; index < secretWord.length(); index++) {
            char character = secretWord.charAt(index);
            if (!isAsciiLetter(character) || guessedLetters.contains(character)) {
                maskedWord.append(character);
            } else {
                maskedWord.append('_');
            }
        }
        return maskedWord.toString();
    }

    public String getDisplayWord() {
        return getMaskedWord();
    }

    public GameState getState() {
        return state;
    }

    public GameState getStatus() {
        return getState();
    }

    public boolean isInProgress() {
        return state == GameState.IN_PROGRESS;
    }

    public boolean isWon() {
        return state == GameState.WON;
    }

    public boolean isLost() {
        return state == GameState.LOST;
    }

    public boolean hasWon() {
        return isWon();
    }

    public boolean hasLost() {
        return isLost();
    }

    public int getMaxIncorrectGuesses() {
        return maxIncorrectGuesses;
    }

    public int getIncorrectGuessesCount() {
        return incorrectGuesses.size();
    }

    public List<Character> getIncorrectGuesses() {
        return Collections.unmodifiableList(incorrectGuesses);
    }

    public Set<Character> getGuessedLetters() {
        return Collections.unmodifiableSet(guessedLetters);
    }

    public int getRemainingGuesses() {
        return Math.max(0, maxIncorrectGuesses - incorrectGuesses.size());
    }

    public int getGuessesLeft() {
        return getRemainingGuesses();
    }

    @Override
    public String toString() {
        return "Hangman{"
                + "word='" + getMaskedWord() + '\''
                + ", state=" + state
                + ", incorrectGuesses=" + incorrectGuesses
                + ", remainingGuesses=" + getRemainingGuesses()
                + '}';
    }

    private void updateState() {
        if (isWordRevealed()) {
            state = GameState.WON;
        } else if (incorrectGuesses.size() >= maxIncorrectGuesses && !incorrectGuesses.isEmpty()) {
            state = GameState.LOST;
        } else {
            state = GameState.IN_PROGRESS;
        }
    }

    private boolean isWordRevealed() {
        for (int index = 0; index < secretWord.length(); index++) {
            char character = secretWord.charAt(index);
            if (isAsciiLetter(character) && !guessedLetters.contains(character)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAsciiLetter(char character) {
        return character >= 'A' && character <= 'Z' || character >= 'a' && character <= 'z';
    }
}
