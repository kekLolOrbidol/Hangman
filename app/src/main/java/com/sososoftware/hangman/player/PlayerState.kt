package com.sososoftware.hangman.player

data class PlayerState (val wordToGuess: String, val guessedLetters: Set<Char> = emptySet()) {
    fun guessLetter(letter: Char): PlayerState =
        PlayerState(wordToGuess, guessedLetters.plus(letter))
}
