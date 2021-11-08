package io.kma.results.readercccd.extension

fun Long?.orZero(): Long {
    return this ?: 0L
}