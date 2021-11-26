package io.kma.results.readercccd.model

class ParsedBarcode(barcode: Barcode) {
    var name = barcode.name
    val text = barcode.text
    val formattedText = barcode.formattedText
    val format = barcode.format
    val date = barcode.date
    val country = barcode.country


}