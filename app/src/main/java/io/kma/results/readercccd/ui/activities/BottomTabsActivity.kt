package io.kma.results.readercccd.ui.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import io.kma.results.readercccd.R
import io.kma.results.readercccd.ui.fragments.ScanBarcodeFromCameraFragment

class BottomTabsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_tabs)
        if (savedInstanceState == null) {
            showInitialFragment()
        }
    }

    private fun showInitialFragment() {
        showFragment()
    }

    private fun showFragment() {
        val fragment = ScanBarcodeFromCameraFragment()
        fragment.apply(::replaceFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_fragment_container, fragment)
            .setReorderingAllowed(true)
            .commit()
    }
}