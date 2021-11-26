package io.kma.results.readercccd.usecase

import io.kma.results.readercccd.model.Barcode
import io.kma.results.readercccd.model.schema.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType

object BarcodeParser {

    fun parseResult(result: Result): Barcode {
        val schema = parseSchema(result.barcodeFormat, result.text)
        return Barcode(
            text = result.text,
            formattedText = schema.toFormattedText(),
            format = result.barcodeFormat,
            schema = schema.schema,
            date = result.timestamp,
            errorCorrectionLevel = result.resultMetadata?.get(ResultMetadataType.ERROR_CORRECTION_LEVEL) as? String,
            country = result.resultMetadata?.get(ResultMetadataType.POSSIBLE_COUNTRY) as? String
        )
    }

    fun parseSchema(format: BarcodeFormat, text: String): Schema {
        if (format != BarcodeFormat.QR_CODE) {
            return Other(text)
        }

        return Other(text)
    }
}