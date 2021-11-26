package io.kma.results.readercccd.utils

import android.graphics.*
import org.jnbis.internal.WsqDecoder
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream

object ImageUtil {

    var JPEG2000_MIME_TYPE = "image/jp2"
    var JPEG2000_ALT_MIME_TYPE = "image/jpeg2000"
    var WSQ_MIME_TYPE = "image/x-wsq"

    @Throws(IOException::class)
    fun decodeImage(inputStream: InputStream, imageLength: Int, mimeType: String): Bitmap {
        var inputStream = inputStream
        synchronized(inputStream) {
            val dataIn = DataInputStream(inputStream)
            val bytes = ByteArray(imageLength)
            dataIn.readFully(bytes)
            inputStream = ByteArrayInputStream(bytes)
        }
        if (JPEG2000_MIME_TYPE.equals(mimeType, ignoreCase = true) || JPEG2000_ALT_MIME_TYPE.equals(mimeType, ignoreCase = true)) {
            val bitmap = org.jmrtd.jj2000.JJ2000Decoder.decode(inputStream)
            return toAndroidBitmap(bitmap)
        } else if (WSQ_MIME_TYPE.equals(mimeType, ignoreCase = true)) {
            val wsqDecoder = WsqDecoder()
            val bitmap = wsqDecoder.decode(inputStream.readBytes())
            val byteData = bitmap.pixels
            val intData = IntArray(byteData.size)
            for (j in byteData.indices) {
                intData[j] = -0x1000000 or ((byteData[j].toInt() and 0xFF) shl 16) or ((byteData[j].toInt() and 0xFF) shl 8) or (byteData[j].toInt() and 0xFF)
            }
            return Bitmap.createBitmap(intData, 0, bitmap.width, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        } else {
            return BitmapFactory.decodeStream(inputStream)
        }
    }

    private fun toAndroidBitmap(bitmap: org.jmrtd.jj2000.Bitmap): Bitmap {
        val intData = bitmap.pixels
        return Bitmap.createBitmap(intData, 0, bitmap.width, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

    }
}