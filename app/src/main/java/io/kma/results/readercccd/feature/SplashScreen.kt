package io.kma.results.readercccd.feature

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import io.kma.results.readercccd.R


class SplashScreen : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

    }


    override fun onResume() {
        super.onResume()
        Handler(Looper.myLooper()!!).postDelayed({
            nextScreen()
        },2000)
    }


    private fun nextScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }



}
