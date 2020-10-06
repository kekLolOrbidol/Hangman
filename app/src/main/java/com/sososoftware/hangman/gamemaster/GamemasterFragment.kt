package com.sososoftware.hangman.gamemaster

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.sososoftware.hangman.MainActivity
import com.sososoftware.hangman.R
import com.sososoftware.hangman.databinding.FragmentHangmanGamemasterBinding
import com.sososoftware.hangman.getGamemasterWords
import com.sososoftware.hangman.utils.LangPref
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [GamemasterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GamemasterFragment : Fragment() {
    lateinit var binding: FragmentHangmanGamemasterBinding
    lateinit var viewModel: GamemasterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_hangman_gamemaster,container,false)
        binding.resetButton.setOnClickListener { viewModel.resetGame() }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.word_length,
            R.layout.count_picker_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.count_picker_item_dropdown)
            binding.spinnerWordLength.adapter = adapter
            binding.spinnerWordLength.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val length = (view as? TextView)?.text?.toString()?.toInt()
                    if (length != null) {
                        viewModel.onPromptLengthChanged(length)
                    }
                }
            }
            binding.spinnerWordLength.setSelection(Companion.DEFAULT_PROMPT_LENGTH - 1)
        }
        val viewModelFactory = GamemasterViewModelFactory(
            DEFAULT_PROMPT_LENGTH
        )
        viewModel = ViewModelProviders.of(this@GamemasterFragment, viewModelFactory)
            .get(GamemasterViewModel::class.java)
        viewModel.state.observe(viewLifecycleOwner, Observer { updateDisplay(it) })
        getWords()
        val lang = LangPref(activity as MainActivity).getLang()
        viewModel.lang = lang.toString()
        return binding.root
    }

    private fun getWords() {
        lifecycleScope.launch {
            if (viewModel.words.isEmpty()) {
                viewModel.words = getGamemasterWords(resources, activity as MainActivity)
            }
        }
    }

    private fun onLetterSelected(state: GamemasterState, newLetter: Char?, index: Int) {
        val newPrompt = state.prompt.mapIndexed {
                promptIndex, oldLetter -> if (promptIndex == index) newLetter else oldLetter
        }
        viewModel.onPromptChange(newPrompt)
    }

    private fun updateDisplay(state: GamemasterState) {
        updateGuess(state)
        buildPrompt(state)
        updateGallows(state)
        buildLetters(state)
    }

    private fun updateGuess(state: GamemasterState) {
        binding.guessText.text = state.guess?.let { guess ->
            when (guess.type) {
                Guess.GuessType.LETTER_GUESS -> "I guess the letter \"${guess.letter}\"."
                Guess.GuessType.WORD_GUESS -> "The word is \"${guess.word}\"."
                Guess.GuessType.GIVE_UP -> "I don't know this word."
                Guess.GuessType.THINKING -> "Thinking..."
            }
        } ?: ""
        binding.rejectGuessButton.isEnabled = state.guess?.letter != null
        binding.rejectGuessButton.setOnClickListener {
            state.guess?.letter?.let { viewModel.onFailedGuess(it) }
        }
    }

    private fun buildPrompt(state: GamemasterState) {
        binding.promptHolder.removeAllViews()
        state.prompt.forEachIndexed{ index, promptLetter ->
            val promptLetterView = layoutInflater.inflate(R.layout.view_prompt_letter, null) as TextView
            promptLetterView.text = promptLetter?.toString() ?: "_"
            promptLetterView.setOnClickListener {
                val dialog = LetterListDialogFragment.newInstance()
                dialog.callback = { letter ->
                    onLetterSelected(state, letter, index)
                }
                dialog.show(parentFragmentManager, "letterPickerDialog")
            }
            binding.promptHolder.addView(promptLetterView)
        }
    }

    private fun buildLetters(state: GamemasterState) {
        val letters = if(LangPref(activity as MainActivity).getLang() == "rus")
            'А'..'Я'
        else 'A'..'Z'
        configLetters(letters, state)
    }

    fun configLetters(letters : CharRange, state: GamemasterState){
        binding.letterHolder.removeAllViews()
        letters.forEach { c ->
            binding.letterHolder.addView(buildLetterView(state, c))
        }
    }

    private fun buildLetterView(state: GamemasterState, c: Char): View {
        val letterView = layoutInflater.inflate(R.layout.view_letter, null) as TextView
        letterView.text = c.toString()
        if (c !in state.guessedLetters){
            letterView.setTextColor(Color.YELLOW)
            letterView.setBackgroundResource(R.drawable.letter_unguessed_background)
            letterView.setOnClickListener { viewModel.onFailedGuess(c) }
        } else {
            letterView.setTextColor(Color.parseColor("white"))
            letterView.setBackgroundResource(
                if (c in state.prompt)
                    R.drawable.letter_correct_guess_background
                else
                    R.drawable.letter_incorrect_guess_background
            )
            letterView.setOnClickListener { viewModel.onLetterUnselected(c) }
        }
        return letterView
    }

    private fun updateGallows(state: GamemasterState) {
        val wrongLetterCount = state.guessedLetters.filter { it !in state.prompt }.size
        binding.gallows.setImageResource(when(wrongLetterCount) {
            0 -> R.drawable.gallows_0
            1 -> R.drawable.gallows_1
            2 -> R.drawable.gallows_2
            3 -> R.drawable.gallows_3
            4 -> R.drawable.gallows_4
            5 -> R.drawable.gallows_5
            6 -> R.drawable.gallows_6
            else -> R.drawable.gallows_7
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment HangmanPlayerFragment.
         */
        @JvmStatic
        fun newInstance() = GamemasterFragment()
        private const val DEFAULT_PROMPT_LENGTH = 6
    }
}