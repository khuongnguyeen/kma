package io.kma.results.readercccd.mlkit

import android.graphics.Bitmap
import android.media.Image
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import io.fotoapparat.preview.Frame
import org.jmrtd.lds.icao.MRZInfo
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

abstract class VisionProcessorBase<T> : VisionImageProcessor {
    private val shouldThrottle = AtomicBoolean(false)
    override fun process(
        data: ByteBuffer, frameMetadata: FrameMetadata, ocrListener: OcrListener
    ) {
        if (shouldThrottle.get()) {
            return
        }
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setWidth(frameMetadata.width)
            .setHeight(frameMetadata.height)
            .setRotation(frameMetadata.rotation)
            .build()
        detectInVisionImage(
            FirebaseVisionImage.fromByteBuffer(data, metadata), frameMetadata, ocrListener
        )
    }

    // Bitmap version
    override fun process(bitmap: Bitmap, ocrListener: OcrListener) {
        if (shouldThrottle.get()) {
            return
        }
        detectInVisionImage(FirebaseVisionImage.fromBitmap(bitmap), null, ocrListener)
    }

    override fun process(image: Image, rotation: Int, ocrListener: OcrListener) {
        if (shouldThrottle.get()) {
            return
        }
        val frameMetadata =
            FrameMetadata.Builder().setWidth(image.width).setHeight(image.height).build()
        val fbVisionImage = FirebaseVisionImage.fromMediaImage(image, rotation)
        detectInVisionImage(fbVisionImage, frameMetadata, ocrListener)
    }

    override fun process(frame: Frame, rotation: Int, ocrListener: OcrListener) {
        if (shouldThrottle.get()) {
            return
        }
        val frameMetadata =
            FrameMetadata.Builder().setWidth(frame.size.width).setHeight(frame.size.height)
                .setRotation(rotation).build()
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setWidth(frameMetadata.width)
            .setHeight(frameMetadata.height)
            .setRotation(frameMetadata.rotation) //??
            .build()
        val fbVisionImage = FirebaseVisionImage.fromByteArray(frame.image, metadata)
        detectInVisionImage(fbVisionImage, frameMetadata, ocrListener)
    }

    private fun detectInVisionImage(
        image: FirebaseVisionImage,
        metadata: FrameMetadata?,
        ocrListener: OcrListener
    ) {
        val start = System.currentTimeMillis()
        detectInImage(image)
            .addOnSuccessListener { results ->
                shouldThrottle.set(false)
                val timeRequired = System.currentTimeMillis() - start
                this@VisionProcessorBase.onSuccess(results, metadata!!, timeRequired, ocrListener)
            }
            .addOnFailureListener { e ->
                shouldThrottle.set(false)
                val timeRequired = System.currentTimeMillis() - start
                this@VisionProcessorBase.onFailure(e, timeRequired, ocrListener)
            }
        shouldThrottle.set(true)
    }

    override fun stop() {}
    protected abstract fun detectInImage(image: FirebaseVisionImage?): Task<T>
    protected abstract fun onSuccess(
        results: T,
        frameMetadata: FrameMetadata,
        timeRequired: Long,
        ocrListener: OcrListener
    )

    protected abstract fun onFailure(e: Exception, timeRequired: Long, ocrListener: OcrListener?)
    interface OcrListener {
        fun onMRZRead(mrzInfo: MRZInfo, timeRequired: Long)
        fun onMRZReadFailure(timeRequired: Long)
        fun onFailure(e: Exception, timeRequired: Long)
    }
}