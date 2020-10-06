package com.sososoftware.hangman.gamemaster

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sososoftware.hangman.utils.LangPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GamemasterViewModel(
    private val initialPromptLength: Int
): ViewModel() {
    val state = MutableLiveData<GamemasterState>()
    var words: List<String> = emptyList()
        set(value) {
            field = value
            wordsMap = words.groupBy { it.length }
            updateGuess()
        }

    private var updateGuessJob: Job? = null
    var lang : String = "eng"
    private var wordsMap: Map<Int,List<String>> = emptyMap()

    init {
        state.value = GamemasterState(initialPromptLength)
        updateGuess()
    }

    fun resetGame() {
        val promptLength = state.value?.prompt?.size ?: initialPromptLength
        state.value = GamemasterState(promptLength)
        updateGuess()
    }

    fun onPromptLengthChanged(length: Int) {
        if (length != state.value?.prompt?.size) {
            state.value = GamemasterState(length)
            updateGuess()
        }
    }

    fun onPromptChange(newPrompt: List<Char?>) {
        state.value = state.value?.updatePrompt(newPrompt)
        updateGuess()
    }

    fun onFailedGuess(letter: Char) {
        state.value = state.value?.addGuessedLetter(letter)
        updateGuess()
    }

    fun onLetterUnselected(letter: Char) {
        state.value = state.value?.removeGuessedLetter(letter)
        updateGuess()
    }

    private fun updateGuess() {
        updateGuessJob?.cancel()
        updateGuessJob = viewModelScope.launch {
            state.value?.let {
                val newGuess = generateGuess(it)
                state.value = it.withGuess(newGuess)
            }
        }
    }

    private suspend fun generateGuess(state: GamemasterState): Guess? =
        withContext(Dispatchers.Default) {
            val letters = if(lang == "eng") ('A'..'Z')
                else ('А'..'Я')
            when {
                wordsMap.isEmpty() -> null
                state.prompt.isFinishedPrompt() -> Guess.guessWord(state.prompt.joinToString(""))
                else -> {
                    val wrongLetters = state.guessedLetters - state.prompt.filterNotNull()
                    val correctLengthWords = wordsMap[state.prompt.size]
                    val validWords = correctLengthWords?.filter {
                        isPossibleWord(it, state.prompt, state.guessedLetters, wrongLetters)
                    } ?: emptyList()
                    when (validWords.size) {
                        0 -> Guess.giveUp()
                        1 -> Guess.guessWord(validWords.first())
                        else -> bestGuess(letters - state.guessedLetters, validWords)
                    }
                }
            }
        }

    // TODO: user should be able to select clever as an option in the settings
    /**
     * As opposed to bestGuess, which selects the remaining letter most likely to be in the word,
     * this selects the letter that splits the possibility space in half.
     * This strategy performs worse than bestGuess overall, but produces more impressive results.
     *
     * It is named cleverGuess because it is too clever by half.
     */
    private fun cleverGuess(guessableLetters: List<Char>, words: List<String>): Guess {
        return when (guessableLetters.size) {
            0 -> Guess.giveUp()
            1 -> Guess.guessLetter(guessableLetters.first())
            else -> {
                val halfWords = kotlin.math.max(1, words.size / 2)
                val letterCounts = guessableLetters.map{ letter ->
                    Pair(letter, words.filter{it.contains(letter)}.size)
                }
                val letter = letterCounts.minBy{ kotlin.math.abs(it.second - halfWords) }?.first
                return letter?.let { Guess.guessLetter(it) } ?: Guess.giveUp()
            }
        }
    }

    private fun bestGuess(guessableLetters: List<Char>, words: List<String>): Guess {
        return when (guessableLetters.size) {
            0 -> Guess.giveUp()
            1 -> Guess.guessLetter(guessableLetters.first())
            else -> {
                val letterCounts = guessableLetters.map{ letter ->
                    Pair(letter, words.filter{it.contains(letter)}.size)
                }
                val letter = letterCounts.maxBy{ it.second }?.first
                return letter?.let { Guess.guessLetter(it) } ?: Guess.giveUp()
            }
        }
    }

    private fun isPossibleWord(
        candidate: String,
        prompt: List<Char?>,
        guessedLetters: Set<Char>,
        wrongLetters: Set<Char>
    ): Boolean {
        return if (candidate.containsAny(wrongLetters) || prompt.size != candidate.length) {
            false
        } else {
            candidate.toList().zip(prompt).map {(wordLetter: Char, promptLetter: Char?) ->
                if (promptLetter == null)
                    wordLetter !in guessedLetters
                else
                    wordLetter == promptLetter
            }.all{it}
        }
    }

    private fun String.containsAny(chars: Collection<Char>): Boolean =
        chars.map{this.contains(it)}.any{it}

    private fun List<Char?>.isFinishedPrompt() = this.all { it != null }
}