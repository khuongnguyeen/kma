package io.kma.results.readercccd.util

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import com.gemalto.jp2.JP2Decoder
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtil {
    @JvmStatic
    fun imageToByteArray(image: Image): ByteArray? {
        var data: ByteArray? = null
        if (image.format == ImageFormat.JPEG) {
            val planes = image.planes
            val buffer = planes[0].buffer
            data = ByteArray(buffer.capacity())
            buffer[data]
            return data
        } else if (image.format == ImageFormat.YUV_420_888) {
            data = NV21toJPEG(
                YUV_420_888toNV21(image),
                image.width, image.height
            )
        }
        return data
    }

    fun YUV_420_888toNV21(image: Image): ByteArray {
        val nv21: ByteArray
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        nv21 = ByteArray(ySize + uSize + vSize)

        //U and V are swapped
        yBuffer[nv21, 0, ySize]
        vBuffer[nv21, ySize, vSize]
        uBuffer[nv21, ySize + vSize, uSize]
        return nv21
    }

    private fun NV21toJPEG(nv21: ByteArray, width: Int, height: Int): ByteArray {
        val out = ByteArrayOutputStream()
        val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        yuv.compressToJpeg(Rect(0, 0, width, height), 100, out)
        return out.toByteArray()
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