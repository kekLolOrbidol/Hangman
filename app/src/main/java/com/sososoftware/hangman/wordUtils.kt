package com.sososoftware.hangman

import android.content.Context
import android.content.res.Resources
import androidx.annotation.RawRes
import com.sososoftware.hangman.utils.LangPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

fun getPlayerWords(resources: Resources, context: Context): List<String> {
    val lang = LangPref(context).getLang()
    return if(lang == "rus" ) getWords(resources, R.raw.rus_words)
        else getWords(resources, R.raw.common_words)
}

fun getDifficultWords(resources: Resources): List<String> = getWords(resources, R.raw.treegle_words)

/**
 * Get the words from the complete English word list.
 * This needs to be done in a coroutine because there are so many words that it
 * can cause a noticeable performance hiccup when loading them all if it's done
 * on the UI thread.
 */
suspend fun getGamemasterWords(resources: Resources, context: Context): List<String> {
    return withContext(Dispatchers.Default) {
        val lang = LangPref(context).getLang()
        if(lang == "rus" ) getWords(resources, R.raw.rus_words)
        else getWords(resources, R.raw.hangman_words)
    }
}

fun getWords(resources: Resources, @RawRes id: Int): List<String> {
    val inputStream = resources.openRawResource(id)
    val inputText = inputStream.bufferedReader().use { it.readText() }
    return inputText.split('\n').map { it.trim().toUpperCase(Locale.getDefault()) }
}