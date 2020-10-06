package com.sososoftware.hangman.utils

import android.content.Context

class CPref(context: Context) {
    private val colorPref = Pref(context).apply { getSp(Companion.ACTION_NAME) }

    fun getColor() : Int? {
        return colorPref.getInt(Companion.ACTION_NAME)
    }

    fun setColor(color : Int){
        colorPref.putInt(Companion.ACTION_NAME, color)
    }

    companion object {
        private const val ACTION_NAME = "color"
    }
}