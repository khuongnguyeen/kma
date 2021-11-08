package io.kma.results.readercccd.feature

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.kma.results.readercccd.R
import io.kma.results.readercccd.activity.CameraOcrReaderActivity
import io.kma.results.readercccd.activity.MtzActivity
import io.kma.results.readercccd.activity.ReadDocActivity
import io.kma.results.readercccd.feature.tabs.BottomTabsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cv_2.setOnClickListener {
            qrScreen()
        }

        cv_1.setOnClickListener {
            mtzScreen()

        }


    }

    private fun qrScreen() {
        val intent = Intent(this, BottomTabsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun mtzScreen() {
        val intent = Intent(this, MtzActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


}