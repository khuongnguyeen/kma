package io.kma.results.readercccd.extension

import androidx.appcompat.app.AppCompatActivity
import io.kma.results.readercccd.feature.common.dialog.ErrorDialogFragment

fun AppCompatActivity.showError(error: Throwable?) {
    val errorDialog =
        ErrorDialogFragment.newInstance(
            this,
            error
        )
    errorDialog.show(supportFragmentManager, "")
}