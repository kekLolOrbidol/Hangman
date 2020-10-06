package com.sososoftware.hangman.options

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jaredrummler.android.colorpicker.ColorShape
import com.sososoftware.hangman.R
import com.sososoftware.hangman.utils.CPref
import com.sososoftware.hangman.utils.LangPref
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), ColorPickerDialogListener {

    var langPref : LangPref? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val color = CPref(this).getColor()
        if(color != -1 && color != null){
            root_settings.setBackgroundColor(color)
            window.statusBarColor = color
        }
        langPref = LangPref(this)
        val lang = langPref?.getLang()
        if(lang == "rus")
            radio_group.check(radio_rus.id)
        if(lang == "eng")
            radio_group.check(radio_eng.id)
        change_background_button.setOnClickListener{
            createColorPickerDialog(0)
        }
        reset_color_button.setOnClickListener{
            onColorSelected(0, getColor(R.color.colorMainBackground))
        }
    }

    private fun createColorPickerDialog(id: Int) {
        ColorPickerDialog.newBuilder()
            .setColor(Color.RED)
            .setDialogType(ColorPickerDialog.TYPE_PRESETS)
            .setAllowCustom(true)
            .setAllowPresets(true)
            .setColorShape(ColorShape.SQUARE)
            .setDialogId(id)
            .show(this)
    }

    override fun onDialogDismissed(dialogId: Int) {
        Toast.makeText(this, "Dialog dismissed", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        when(radio_group.checkedRadioButtonId){
            radio_rus.id -> langPref?.setLang("rus")
            radio_eng.id -> langPref?.setLang("eng")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onColorSelected(dialogId: Int, color: Int) {
        CPref(this).setColor(color)
        root_settings.setBackgroundColor(color)
        window.statusBarColor = color
    }
}