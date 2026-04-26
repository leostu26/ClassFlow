package com.classflow

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The window background (splash_screen drawable = purple + ClassFlow icon) is visible
        // immediately via the Theme.ClassFlow.Splash windowBackground attribute, before
        // setContentView finishes. On Android 12+, the OS splash (plain purple, no icon)
        // exits with a fade animation revealing this window below.
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }
}
