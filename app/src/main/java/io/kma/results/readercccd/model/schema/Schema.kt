package io.kma.results.readercccd.model.schema

enum class BarcodeSchema {
    OTHER;
}

interface Schema {
    val schema: BarcodeSchema
    fun toFormattedText(): String
    fun toBarcodeText(): String
}