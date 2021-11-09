package io.kma.results.readercccd.mlkit

import android.graphics.Bitmap
import android.media.Image
import com.google.firebase.ml.common.FirebaseMLException
import io.fotoapparat.preview.Frame
import io.kma.results.readercccd.mlkit.VisionProcessorBase.OcrListener
import java.nio.ByteBuffer

interface VisionImageProcessor {
    @Throws(FirebaseMLException::class)
    fun process(data: ByteBuffer, frameMetadata: FrameMetadata, ocrListener: OcrListener)
    fun process(bitmap: Bitmap, ocrListener: OcrListener)
    fun process(bitmap: Image, rotation: Int, ocrListener: OcrListener)
    fun process(frame: Frame, rotation: Int, ocrListener: OcrListener)
    fun stop()
}