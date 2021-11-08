package io.kma.results.readercccd.extension

import androidx.fragment.app.Fragment
import io.kma.results.readercccd.feature.common.dialog.ErrorDialogFragment

fun Fragment.showError(error: Throwable?) {
    val errorDialog = ErrorDialogFragment.newInstance(requireContext(), error)
    errorDialog.show(childFragmentManager, "")
}
