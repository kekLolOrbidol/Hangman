package com.sososoftware.hangman.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayerViewModel(private val words: List<String>): ViewModel() {

    val state = MutableLiveData<PlayerState>()

    init {
        state.value = PlayerState(getWord())
    }

    fun resetGame() {
        state.value = PlayerState(getWord())
    }

    fun onGuess(letter: Char) {
        state.value = state.value?.guessLetter(letter)
    }

    private fun getWord(): String = words[(0..words.size).random()]
}