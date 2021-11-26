package io.kma.results.readercccd.model

import io.kma.results.readercccd.model.schema.BarcodeSchema
import com.google.zxing.BarcodeFormat
import java.io.Serializable

data class Barcode(
    val name: String? = null,
    val text: String,
    val formattedText: String,
    val format: BarcodeFormat,
    val schema: BarcodeSchema,
    val date: Long,
    val isGenerated: Boolean = false,
    val isFavorite: Boolean = false,
    val errorCorrectionLevel: String? = null,
    val country: String? = null
) : Serializable