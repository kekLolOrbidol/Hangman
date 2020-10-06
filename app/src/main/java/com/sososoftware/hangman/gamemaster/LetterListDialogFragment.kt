package com.sososoftware.hangman.gamemaster

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.sososoftware.hangman.MainActivity
import com.sososoftware.hangman.R
import com.sososoftware.hangman.databinding.FragmentLetterListDialogBinding
import com.sososoftware.hangman.utils.LangPref

class LetterListDialogFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentLetterListDialogBinding
    var callback: (letter: Char?) -> Unit = { }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_letter_list_dialog,container,false)
        buildLetters()
        return binding.root
    }

    private fun buildLetters() {
        if(LangPref(activity as MainActivity).getLang() == "rus"){
            ('А'..'Я').forEach{
                configLetter(it)
            }
        }
        else {
            ('A'..'Z').forEach {
                configLetter(it)
            }
        }
    }

    fun configLetter(c : Char){
        val letterView = layoutInflater.inflate(R.layout.view_letter, null) as TextView
        letterView.text = c.toString()
        letterView.setOnClickListener {
            callback(c)
            dismiss()
        }
        binding.list.addView(letterView)
    }

    companion object {
        fun newInstance(): LetterListDialogFragment = LetterListDialogFragment()
    }
}