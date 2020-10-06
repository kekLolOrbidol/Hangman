package com.sososoftware.hangman.gamemaster

data class Guess(val letter: Char?, val word: String?, val type: GuessType) {
    enum class GuessType {
        LETTER_GUESS,
        WORD_GUESS,
        GIVE_UP,
        THINKING
    }

    companion object {
        fun guessLetter(letter: Char) = Guess(letter, null, GuessType.LETTER_GUESS)
        fun guessWord(word: String) = Guess(null, word, GuessType.WORD_GUESS)
        fun giveUp() = Guess(null, null, GuessType.GIVE_UP)
        fun thinking() = Guess(null, null, GuessType.THINKING)
    }
}