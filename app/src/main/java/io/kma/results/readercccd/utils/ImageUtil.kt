package io.kma.results.readercccd.utils

import android.content.Context
import android.graphics.*
import android.media.Image
import android.util.Log
import androidx.annotation.Nullable
import com.gemalto.jp2.JP2Decoder

import org.jnbis.internal.WsqDecoder

import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

import jj2000.j2k.decoder.Decoder
import jj2000.j2k.util.ParameterList


import org.jmrtd.lds.ImageInfo.WSQ_MIME_TYPE
import kotlin.experimental.and

object ImageUtil {

    var JPEG2000_MIME_TYPE = "image/jp2"
    var JPEG2000_ALT_MIME_TYPE = "image/jpeg2000"
    var WSQ_MIME_TYPE = "image/x-wsq"




    @Throws(IOException::class)
    fun decodeImage(inputStream: InputStream, imageLength: Int, mimeType: String): Bitmap {
        var inputStream = inputStream
        /* DEBUG */
        synchronized(inputStream) {
            val dataIn = DataInputStream(inputStream)
            val bytes = ByteArray(imageLength)
            dataIn.readFully(bytes)
            inputStream = ByteArrayInputStream(bytes)
        }
        /* END DEBUG */

        if (JPEG2000_MIME_TYPE.equals(mimeType, ignoreCase = true) || JPEG2000_ALT_MIME_TYPE.equals(mimeType, ignoreCase = true)) {
            val bitmap = org.jmrtd.jj2000.JJ2000Decoder.decode(inputStream)
            return toAndroidBitmap(bitmap)
        } else if (WSQ_MIME_TYPE.equals(mimeType, ignoreCase = true)) {
            //org.jnbis.Bitmap bitmap = WSQDecoder.decode(inputStream);
            val wsqDecoder = WsqDecoder()
            val bitmap = wsqDecoder.decode(inputStream.readBytes())
            val byteData = bitmap.pixels
            val intData = IntArray(byteData.size)
            for (j in byteData.indices) {
                intData[j] = -0x1000000 or ((byteData[j].toInt() and 0xFF) shl 16) or ((byteData[j].toInt() and 0xFF) shl 8) or (byteData[j].toInt() and 0xFF)
            }
            return Bitmap.createBitmap(intData, 0, bitmap.width, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            //return toAndroidBitmap(bitmap);
        } else {
            return BitmapFactory.decodeStream(inputStream)
        }
    }


    private fun toAndroidBitmap(bitmap: org.jmrtd.jj2000.Bitmap): Bitmap {
        val intData = bitmap.pixels
        return Bitmap.createBitmap(intData, 0, bitmap.width, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

    }


    ///RCC
    fun decode(`in`: InputStream?): Bitmap? {
        val width = 307
        val height = 378
        var ret: Bitmap? = null
        println("En ImageUtil.decode init")
        try {
            val decoder = JP2Decoder(`in`)
            val header = decoder.readHeader()
            var skipResolutions = 1
            var imgWidth = header.width
            var imgHeight = header.height
            println("imgWidth:$imgWidth imgHeight:$imgHeight")
            while (skipResolutions < header.numResolutions) {
                imgWidth = imgWidth shr 1
                imgHeight = imgHeight shr 1
                if (imgWidth < width || imgHeight < height) break else skipResolutions++
            }
            skipResolutions--
            if (skipResolutions > 0) decoder.setSkipResolutions(skipResolutions)
            println("En ImageUtil.decode antes de decode")
            ret = decoder.decode()
        } catch (e: Exception) {
            println("En ImageUtil.decode e:" + e.message)
        }
        return ret
    }
}