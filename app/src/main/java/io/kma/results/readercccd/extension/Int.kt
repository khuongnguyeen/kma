package io.kma.results.readercccd.extension

fun Int?.orZero(): Int {
    return this ?: 0
}