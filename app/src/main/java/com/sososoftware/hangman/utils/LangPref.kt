package com.sososoftware.hangman.utils

import android.content.Context

class LangPref(context: Context){
    private val ACTION_NAME = "lang"
    private val langPref = Pref(context).apply { getSp(ACTION_NAME) }

    fun getLang() : String? {
        return langPref.getStr(ACTION_NAME)
    }

    fun setLang(lang : String){
        langPref.putStr(ACTION_NAME, lang)
    }
}