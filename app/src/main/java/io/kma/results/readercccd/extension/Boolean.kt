package io.kma.results.readercccd.extension

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}