package com.sososoftware.hangman.gamemaster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GamemasterViewModelFactory(
    private val initialPromptLength: Int
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GamemasterViewModel::class.java)) {
            return GamemasterViewModel(initialPromptLength) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}