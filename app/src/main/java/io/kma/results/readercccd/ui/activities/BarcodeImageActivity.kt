package io.kma.results.readercccd.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import io.kma.results.readercccd.R
import io.kma.results.readercccd.extension.applySystemWindowInsets
import io.kma.results.readercccd.extension.toStringId
import io.kma.results.readercccd.extension.unsafeLazy
import io.kma.results.readercccd.common.barcodeImageGenerator
import io.kma.results.readercccd.model.Barcode
import io.kma.results.readercccd.usecase.Logger
import kotlinx.android.synthetic.main.activity_qr_image.*
import java.text.SimpleDateFormat
import java.util.*

class BarcodeImageActivity : BaseActivity() {

    companion object {
        private const val BARCODE_KEY = "BARCODE_KEY"

        fun start(context: Context, barcode: Barcode) {
            val intent = Intent(context, BarcodeImageActivity::class.java)
            intent.putExtra(BARCODE_KEY, barcode)
            context.startActivity(intent)
        }
    }

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    private val barcode by unsafeLazy {
        intent?.getSerializableExtra(BARCODE_KEY) as? Barcode ?: throw IllegalArgumentException("No barcode passed")
    }
    private var originalBrightness: Float = 0.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_image)
        supportEdgeToEdge()
        saveOriginalBrightness()
        handleToolbarBackPressed()
        handleToolbarMenuItemClicked()
        showMenu()
        showBarcode()
    }

    private fun supportEdgeToEdge() {
        root_view.applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun saveOriginalBrightness() {
        originalBrightness = window.attributes.screenBrightness
    }

    private fun handleToolbarBackPressed() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleToolbarMenuItemClicked() {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_increase_brightness -> {
                    increaseBrightnessToMax()
                    toolbar.menu.apply {
                        findItem(R.id.item_increase_brightness).isVisible = false
                        findItem(R.id.item_decrease_brightness).isVisible = true
                    }
                }
                R.id.item_decrease_brightness -> {
                    restoreOriginalBrightness()
                    toolbar.menu.apply {
                        findItem(R.id.item_decrease_brightness).isVisible = false
                        findItem(R.id.item_increase_brightness).isVisible = true
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun showMenu() {
        toolbar.inflateMenu(R.menu.menu_barcode_image)
    }

    private fun showBarcode() {
        showBarcodeImage()
        showBarcodeDate()
        showBarcodeFormat()
        showBarcodeText()
    }

    private fun showBarcodeImage() {
        try {
            val bitmap = barcodeImageGenerator.generateBitmap(barcode, 2000, 2000, 0)
            image_view_barcode.setImageBitmap(bitmap)

        } catch (ex: Exception) {
            Logger.log(ex)
            image_view_barcode.isVisible = false
        }
    }

    private fun showBarcodeDate() {
        text_view_date.text = dateFormatter.format(barcode.date)
    }

    private fun showBarcodeFormat() {
        val format = barcode.format.toStringId()
        toolbar.setTitle(format)
    }

    private fun showBarcodeText() {
        text_view_barcode_text.text = barcode.text
    }

    private fun increaseBrightnessToMax() {
        setBrightness(1.0f)
    }

    private fun restoreOriginalBrightness() {
        setBrightness(originalBrightness)
    }

    private fun setBrightness(brightness: Float) {
        window.attributes = window.attributes.apply {
            screenBrightness = brightness
        }
    }
}