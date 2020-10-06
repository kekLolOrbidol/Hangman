package com.sososoftware.hangman.player

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sososoftware.hangman.MainActivity
import com.sososoftware.hangman.R
import com.sososoftware.hangman.databinding.FragmentHangmanPlayerBinding
import com.sososoftware.hangman.getPlayerWords
import com.sososoftware.hangman.utils.LangPref

/**
 * A simple [Fragment] subclass.
 * Use the [PlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayerFragment : Fragment() {
    lateinit var binding: FragmentHangmanPlayerBinding
    lateinit var viewModel: PlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_hangman_player,container,false)
        binding.resetButton.setOnClickListener { viewModel.resetGame() }
        // TODO: Add support for getting difficult words instead
        // TODO: Add support for getting only words of specified length
        val viewModelFactory = PlayerViewModelFactory(getPlayerWords(resources, activity as MainActivity))
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(PlayerViewModel::class.java)
        viewModel.state.observe(viewLifecycleOwner, Observer { updateDisplay(it) })
        return binding.root
    }

    private fun updateDisplay(state: PlayerState) {
        updatePrompt(state)
        updateGallows(state)
        buildLetters(state)
    }

    private fun updatePrompt(state: PlayerState) {
        binding.wordHolder.text = state.wordToGuess.toCharArray().joinToString(" ") {
            if (it in state.guessedLetters) it.toString() else "_"
        }
    }

    private fun buildLetters(state: PlayerState) {
        val letters = if(LangPref(activity as MainActivity).getLang() == "rus")
            'А'..'Я'
        else 'A'..'Z'
        configLetters(letters, state)
    }

    private fun configLetters(letters : CharRange, state: PlayerState){
        binding.letterHolder.removeAllViews()
        letters.forEach { c ->
            binding.letterHolder.addView(buildLetterView(state, c))
        }
    }

    private fun buildLetterView(state: PlayerState, c: Char): View {
        val letterView = layoutInflater.inflate(R.layout.view_letter, null) as TextView
        letterView.text = c.toString()
        if (c !in state.guessedLetters){
            letterView.setTextColor(Color.YELLOW)
            letterView.setBackgroundResource(R.drawable.letter_unguessed_background)
            letterView.setOnClickListener { viewModel.onGuess(c) }
        } else {
            letterView.setTextColor(Color.parseColor("white"))
            letterView.setBackgroundResource(
                if (c in state.wordToGuess)
                    R.drawable.letter_correct_guess_background
                else
                    R.drawable.letter_incorrect_guess_background
            )
        }
        return letterView
    }

    private fun updateGallows(state: PlayerState) {
        val wrongLetterCount = state.guessedLetters.filter { it !in state.wordToGuess }.size
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
        fun newInstance() =
            PlayerFragment()
    }
}