package com.example.hangman;

/** The outcome of attempting a letter in a Hangman game. */
public enum GuessResult {
    INVALID,
    CORRECT,
    INCORRECT,
    DUPLICATE,
    GAME_OVER;

    public boolean isValid() {
        return this != INVALID;
    }

    public boolean isCorrect() {
        return this == CORRECT;
    }

    public boolean isIncorrect() {
        return this == INCORRECT;
    }

    public boolean isDuplicate() {
        return this == DUPLICATE;
    }
}
