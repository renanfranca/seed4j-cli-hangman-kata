package com.renanfranca.hangman;

/** The outcome of attempting a guess. */
public enum GuessResult {
    CORRECT,
    INCORRECT,
    INVALID,
    DUPLICATE,
    GAME_OVER
}
