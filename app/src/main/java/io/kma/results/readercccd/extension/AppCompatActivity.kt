package io.kma.results.readercccd.extension

import android.content.Context
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import io.kma.results.readercccd.R
import io.kma.results.readercccd.common.dialog.ErrorDialogFragment
import io.kma.results.readercccd.model.Barcode
import java.text.DateFormat
import java.util.*

fun AppCompatActivity.showError(error: Throwable?) {
    val errorDialog =
            ErrorDialogFragment.newInstance(
                    this,
                    error
            )
    errorDialog.show(supportFragmentManager, "")
}

fun BarcodeFormat.toStringId(): Int {
    return when (this) {
        BarcodeFormat.AZTEC -> R.string.barcode_format_aztec
        BarcodeFormat.CODABAR -> R.string.barcode_format_codabar
        BarcodeFormat.CODE_39 -> R.string.barcode_format_code_39
        BarcodeFormat.CODE_93 -> R.string.barcode_format_code_93
        BarcodeFormat.CODE_128 -> R.string.barcode_format_code_128
        BarcodeFormat.DATA_MATRIX -> R.string.barcode_format_data_matrix
        BarcodeFormat.EAN_8 -> R.string.barcode_format_ean_8
        BarcodeFormat.EAN_13 -> R.string.barcode_format_ean_13
        BarcodeFormat.ITF -> R.string.barcode_format_itf_14
        BarcodeFormat.PDF_417 -> R.string.barcode_format_pdf_417
        BarcodeFormat.QR_CODE -> R.string.barcode_format_qr_code
        BarcodeFormat.UPC_A -> R.string.barcode_format_upc_a
        BarcodeFormat.UPC_E -> R.string.barcode_format_upc_e
        else -> R.string.barcode_format_qr_code
    }
}


fun Boolean?.orFalse(): Boolean {
    return this ?: false
}

val Context.vibrator: Vibrator?
    get() = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

val Context.wifiManager: WifiManager?
    get() = applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

val Context.currentLocale: Locale?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales.get(0)
    } else {
        resources.configuration.locale
    }


fun DateFormat.parseOrNull(date: String?): Date? {
    return try {
        parse(date.orEmpty())
    } catch (ex: Exception) {
        null
    }
}

fun List<DateFormat>.parseOrNull(date: String?): Date? {
    forEach { dateParser ->
        val parsedDate = dateParser.parseOrNull(date)
        if (parsedDate != null) {
            return parsedDate
        }
    }
    return null
}

fun DateFormat.formatOrNull(time: Long?): String? {
    return try {
        format(Date(time!!))
    } catch (ex: Exception) {
        null
    }
}


fun Fragment.showError(error: Throwable?) {
    val errorDialog = ErrorDialogFragment.newInstance(requireContext(), error)
    errorDialog.show(childFragmentManager, "")
}

fun Int?.orZero(): Int {
    return this ?: 0
}

fun Long?.orZero(): Long {
    return this ?: 0L
}

fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

fun Result.equalTo(barcode: Barcode?): Boolean {
    return barcodeFormat == barcode?.format && text == barcode?.text
}

fun StringBuilder.appendIfNotNullOrBlank(prefix: String = "", value: String?, suffix: String = ""): StringBuilder {
    if (value.isNullOrBlank().not()) {
        append(prefix)
        append(value)
        append(suffix)
    }
    return this
}

fun Uri.Builder.appendQueryParameterIfNotNullOrBlank(key: String, value: String?): Uri.Builder {
    if (value.isNullOrBlank().not()) {
        appendQueryParameter(key, value)
    }
    return this
}
