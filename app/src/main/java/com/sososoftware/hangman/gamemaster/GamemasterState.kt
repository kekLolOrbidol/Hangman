package com.sososoftware.hangman.gamemaster

data class GamemasterState (
    var prompt: List<Char?>,
    var guessedLetters: Set<Char> = emptySet(),
    var guess: Guess? = null
) {
    constructor(promptLength: Int) : this(arrayOfNulls<Char?>(promptLength).toList())

    fun withGuess(guess: Guess?) = GamemasterState(prompt, guessedLetters, guess)

    fun updatePrompt(newPrompt: List<Char?>) =
        GamemasterState(
            newPrompt,
            guessedLetters + newPrompt.filterNotNull(),
            Guess.thinking()
        )

    fun addGuessedLetter(letter: Char) =
        GamemasterState(prompt, guessedLetters + letter, Guess.thinking())

    fun removeGuessedLetter(letter: Char): GamemasterState =
        if (letter !in prompt) {
            GamemasterState(prompt, guessedLetters - letter, Guess.thinking())
        } else {
            this
        }
}
