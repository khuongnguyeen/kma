package io.kma.results.readercccd.model.schema

import io.kma.results.readercccd.extension.startsWithAnyIgnoreCase

data class GoogleMaps(val url: String) : Schema {

    companion object {
        private val PREFIXES = listOf("http://maps.google.com/", "https://maps.google.com/")

        fun parse(text: String): GoogleMaps? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }
            return GoogleMaps(text)
        }
    }

    override val schema = BarcodeSchema.GOOGLE_MAPS
    override fun toFormattedText(): String = url
    override fun toBarcodeText(): String = url
}