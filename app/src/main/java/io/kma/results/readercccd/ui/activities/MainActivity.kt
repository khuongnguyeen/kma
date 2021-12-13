package io.kma.results.readercccd.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import io.kma.results.readercccd.R
import io.kma.results.readercccd.ui.ManualSheet
import io.kma.results.readercccd.ui.SliderAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {


    private var currentPage = 0
    private var numPages = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
        val assets = listOf(
                R.drawable.banner_qr,
                R.drawable.banner_ui_12,
                R.drawable.ocr_v2
        )
        createSlider(assets)
        cv_2.setOnClickListener {
            cv_2.isEnabled = false
            qrScreen()
            Handler(Looper.myLooper()!!).postDelayed({
                cv_2.isEnabled = true
            }, 1000)
        }
        cv_1.setOnClickListener {
            cv_1.isEnabled = false
            val checkedId = radio_group.checkedRadioButtonId
            if (checkedId == R.id.camera) {
                ocrScreen()
            } else {
                ManualSheet().show(supportFragmentManager, "dialog")
            }
            Handler(Looper.myLooper()!!).postDelayed({
                cv_1.isEnabled = true
            }, 1000)
        }

    }


    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }


    private fun createSlider(string: List<Int>) {
        vpSlider.adapter = SliderAdapter(this, string)
        indicator.setViewPager(vpSlider)
        val density = resources.displayMetrics.density
        indicator.radius = 5 * density
        numPages = string.size
        val update = Runnable {
            if (currentPage == numPages) {
                currentPage = 0
            }
            vpSlider.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post(update)
            }
        }, 2000, 2000)
        indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(pos: Int) {}
        })
    }


    private fun qrScreen() {
        val intent = Intent(this, QRTabsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun ocrScreen() {
        val intent = Intent(this, CameraOcrReaderActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


}